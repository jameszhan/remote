/*

package com.mulberry.remote.xml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;

import javax.xml.bind.*;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class JaxbSerDes {
    private enum State {
        INITIALIZING, OPERATIONAL, ERROR
    }

    private static final String JAXB_CONTEXT_FILE_NAME = "jaxb.index";
    private static final String RESOURCE_SEARCH_PATH_PREFIX = "com/apple/www";
    */
    //private static final String RESOURCE_SEARCH_PATH = "classpath*:" + RESOURCE_SEARCH_PATH_PREFIX + "/**/"  + JAXB_CONTEXT_FILE_NAME;
/*
    private static final String JAXB_SERIALIZER_IN_ERROR_STATE = "JaxbSerializer in error state";
    private static final Logger LOGGER = LoggerFactory.getLogger(JaxbSerDes.class);
    private static final Map<String, JaxbSerDes> REPOSITORY = new HashMap<String, JaxbSerDes>();
    private static final ReadWriteLock READWRITE_LOCK = new ReentrantReadWriteLock(true);

    private static int s_minUnmarshallers = 0;
    private static int s_minMarshallers = 0;
    private static int s_maxUnmarshallers = Integer.MAX_VALUE;
    private static int s_maxMarshallers = Integer.MAX_VALUE;

    private final Object stateControl = new Object();
    private State state = State.INITIALIZING;
    private String contextPackages = null;
    private JAXBContext jaxbContext = null;
    private BlockingQueue<Unmarshaller> availableUnMarshallers = new LinkedBlockingQueue<Unmarshaller>();
    private BlockingQueue<Marshaller> availableMarshallers = new LinkedBlockingQueue<Marshaller>();
    private int currentUnMarshallers = 0;
    private int currentMarshallers = 0;
    private boolean pooling = true;
    private boolean enableEventHandler = false;
    private final Map<String, Object> marshallerProperties = new ConcurrentHashMap<String, Object>();
    private final Map<String, Object> unmarshallerProperties = new ConcurrentHashMap<String, Object>();

    protected JaxbSerDes(String contextPackages) {
        this.contextPackages = contextPackages;
    }

    protected void createJaxbContext() {
        synchronized (stateControl) {
            if (null == jaxbContext) {
                try {
                    jaxbContext = JAXBContext.newInstance(contextPackages);
                    state = State.OPERATIONAL;
                } catch (Throwable t) {
                    state = State.ERROR;
                    LOGGER.error("Exeption creating JAXBContext for context: " + contextPackages, t);
                } finally {
                    stateControl.notifyAll();
                }
                if (null != jaxbContext) {
                    LOGGER.info("Created JAXBContext for context: " + contextPackages + " in  millis.");
                }
            } else {
                LOGGER.warn("Attempt to create a jaxbcontext when one already exists, ignoring, existing context: "
                        + contextPackages);
            }
        }
    }

    public static JaxbSerDes getInstanceForContext(Class<?> clazz) {
        return getInstanceForContext(clazz.getPackage().getName());
    }


    public static JaxbSerDes getInstanceForContextFromClasspath(){
        synchronized (JaxbSerDes.class) {
            try {
                return getInstanceForContext(findJAXBContextPath());
            } catch (IOException e) {
                throw new IllegalStateException(JAXB_SERIALIZER_IN_ERROR_STATE, e);
            }
        }
    }

    public static String findJAXBContextPath() throws IOException {

        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        StringBuilder sb = new StringBuilder();
        char c = ':';
        // search the jaxb.index file under com/apple/www in classpath.
        Resource[] resources = resolver.getResources(RESOURCE_SEARCH_PATH);
        for (int i = 0; i < resources.length; i++) {
            if (sb.length() > 0) {
                sb.append(c);
            }
            Resource resource = resources[i];
            URL url = resource.getURL();
            String path = url.getPath();
            // get the beginning position of package.
            int start = path.lastIndexOf(RESOURCE_SEARCH_PATH_PREFIX);
            if (start >= 0) {
                String s = path.substring(start, path.length() - JAXB_CONTEXT_FILE_NAME.length() - 1);
                String packageName = StringUtils.replace(s, "/", ".");
                sb.append(packageName);
            }
        }
        return sb.toString();
    }

    public static JaxbSerDes getInstanceForContext(String contextPackages) {
        Lock rlock = READWRITE_LOCK.readLock();
        Lock wlock = READWRITE_LOCK.writeLock();
        rlock.lock();
        boolean isReadLocked = true;
        try {
            JaxbSerDes serializer = REPOSITORY.get(contextPackages);
            if (null != serializer) {
                return serializer;
            }

            rlock.unlock();
            isReadLocked = false;

            wlock.lock();
            // Now write locked, do the minimum necessary in here
            boolean isNewSerializer = false;
            try {
                serializer = REPOSITORY.get(contextPackages); // recheck
                if (null == serializer) {
                    serializer = new JaxbSerDes(contextPackages);
                    isNewSerializer = true;
                    REPOSITORY.put(contextPackages, serializer);
                }
            } finally {
                wlock.unlock();
            }
            // if we created a new instance, have it create its underlying
            // jaxb context here, where it will not block anything other than
            // use of this particular serialzer (which needs to be that way)
            if (isNewSerializer) {
                serializer.createJaxbContext();
            }
            return serializer;
        } finally {
            if (isReadLocked) {
                rlock.unlock();
            }
        }
    }


    public Object unmarshalFastInfosetStax(byte[] messageBytes) throws Exception {
        XMLStreamReader streamReader = new StAXDocumentParser(new ByteArrayInputStream(messageBytes));
        try {
            return unmarshal(streamReader);
        } finally {
            streamReader.close();
            // does not close underlying input stream, but no need to
        }
    }


    public Object unmarshalFastInfosetSax(byte[] messageBytes) throws Exception {
        SAXDocumentParser parser = new SAXDocumentParser();
        ByteArrayInputStream in = new ByteArrayInputStream(messageBytes);
        SAXSource src = new SAXSource(parser, new InputSource(in));
        try {
            return unmarshal(src);
        } finally {
            in.close();
        }
    }


    public Object unmarshalFastInfosetTwoPass(byte[] messageBytes) throws Exception {
        // Pass 1 : fi bytes unmarshalled into xml bytes
        final TransformerFactory factory = TransformerFactory.newInstance();
        ByteArrayInputStream baisFi = new ByteArrayInputStream(messageBytes);
        ByteArrayOutputStream baosXml = new ByteArrayOutputStream();
        Transformer tx = factory.newTransformer();
        tx.transform(new FastInfosetSource(baisFi), new StreamResult(baosXml));

        byte[] xmlBytes = baosXml.toByteArray();

        // Pass 2 : xml bytes unmarhalled into the serialable object
        return unmarshal(xmlBytes);
    }


    public byte[] marshalFastInfosetStax(Object object) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLStreamWriter writer = new StAXDocumentSerializer(baos);
        try {
            marshal(object, writer);
        } finally {
            writer.close();
        }
        byte[] byteData = baos.toByteArray();

        return byteData;
    }

    public byte[] marshalFastInfosetSax(Object object) throws Exception {
        SAXDocumentSerializer serializer = new SAXDocumentSerializer();
        ByteArrayOutputStream writer = new ByteArrayOutputStream();
        serializer.setOutputStream(writer);
        ContentHandler ch = serializer;

        try {
            marshal(object, ch);
            return writer.toByteArray();
        } finally {
            writer.close();
        }
    }

    public byte[] marshalFastInfosetTwoPass(Object object) throws Exception {
        // Pass 1: marshal serializable to xml bytes
        byte[] xmlBytes = marshal(object);
        // Pass 2: Covert xml bytes to fastinfoset
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final TransformerFactory factory = TransformerFactory.newInstance();
        Transformer tx = factory.newTransformer();
        tx.transform(new StreamSource(new ByteArrayInputStream(xmlBytes)), new FastInfosetResult(baos));
        byte[] byteData = baos.toByteArray();
        return byteData;
    }

    public Object unmarshal(byte[] messageBytes) throws Exception {
        InputStream inputStream = new ByteArrayInputStream(messageBytes);
        Object object = unmarshal(inputStream);
        inputStream.close();
        return object;
    }

    public byte[] marshal(Object object) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        marshal(object, outputStream);
        byte[] byteData = outputStream.toByteArray();
        outputStream.close();
        return byteData;
    }

    public void marshal(Object object, File file) throws Exception {
        Marshaller marshaller = (pooling) ? getAvailableMarshaller() : createNewMarshaller();
        try {
            marshaller.marshal(object, file);
        } finally {
            if (pooling) {
                availableMarshallers.put(marshaller);
            }
        }
    }


    public void marshal(Object object, Node node) throws Exception {
        Marshaller marshaller = (pooling) ? getAvailableMarshaller() : createNewMarshaller();
        try {
            marshaller.marshal(object, node);
        } finally {
            if (pooling) {
                availableMarshallers.put(marshaller);
            }
        }
    }


    public void marshal(Object object, OutputStream outputStream) throws Exception {
        Marshaller marshaller = (pooling) ? getAvailableMarshaller() : createNewMarshaller();
        try {
            marshaller.marshal(object, outputStream);
        } finally {
            if (pooling) {
                availableMarshallers.put(marshaller);
            }
        }
    }


    public void marshal(Object object, ContentHandler handler) throws Exception {
        Marshaller marshaller = (pooling) ? getAvailableMarshaller() : createNewMarshaller();
        try {
            marshaller.marshal(object, handler);
        } finally {
            if (pooling) {
                availableMarshallers.put(marshaller);
            }
        }
    }


    public void marshal(Object object, Writer writer) throws Exception {

        Marshaller marshaller = (pooling) ? getAvailableMarshaller() : createNewMarshaller();
        try {
            marshaller.marshal(object, writer);
        } finally {
            if (pooling) {
                availableMarshallers.put(marshaller);
            }
        }
    }


    public void marshal(Object object, Result result) throws Exception {

        Marshaller marshaller = (pooling) ? getAvailableMarshaller() : createNewMarshaller();
        try {
            marshaller.marshal(object, result);
        } finally {
            if (pooling) {
                availableMarshallers.put(marshaller);
            }
        }
    }

    public void marshal(Object object, XMLEventWriter writer) throws Exception {

        Marshaller marshaller = (pooling) ? getAvailableMarshaller() : createNewMarshaller();
        try {
            marshaller.marshal(object, writer);
        } finally {
            if (pooling) {
                availableMarshallers.put(marshaller);
            }
        }
    }


    public void marshal(Object object, XMLStreamWriter writer) throws Exception {

        Marshaller marshaller = (pooling) ? getAvailableMarshaller() : createNewMarshaller();
        try {
            marshaller.marshal(object, writer);
        } finally {
            if (pooling) {
                availableMarshallers.put(marshaller);
            }
        }
    }

    public Object unmarshal(File file) throws Exception {
        Unmarshaller unMarshaller = (pooling) ? getAvailableUnmarshaller() : createNewUnmarshaller();
        try {
            return unMarshaller.unmarshal(file);
        } finally {
            if (pooling) {
                availableUnMarshallers.put(unMarshaller);
            }
        }
    }


    public Object unmarshal(InputStream inputStream) throws Exception {
        Unmarshaller unMarshaller = (pooling) ? getAvailableUnmarshaller() : createNewUnmarshaller();
        try {
            return unMarshaller.unmarshal(inputStream);
        } finally {
            if (pooling) {
                availableUnMarshallers.put(unMarshaller);
            }
        }
    }

    public Object unmarshal(Node node) throws Exception {

        Unmarshaller unMarshaller = (pooling) ? getAvailableUnmarshaller() : createNewUnmarshaller();
        try {
            return unMarshaller.unmarshal(node);
        } finally {
            if (pooling) {
                availableUnMarshallers.put(unMarshaller);
            }
        }
    }


    public Object unmarshal(Source source) throws Exception {
        Unmarshaller unMarshaller = (pooling) ? getAvailableUnmarshaller() : createNewUnmarshaller();
        try {
            return unMarshaller.unmarshal(source);
        } finally {
            if (pooling) {
                availableUnMarshallers.put(unMarshaller);
            }
        }
    }

    public Object unmarshal(URL url) throws Exception {
        Unmarshaller unMarshaller = (pooling) ? getAvailableUnmarshaller() : createNewUnmarshaller();
        try {
            return unMarshaller.unmarshal(url);
        } finally {
            if (pooling) {
                availableUnMarshallers.put(unMarshaller);
            }
        }
    }

    public Object unmarshal(XMLEventReader reader) throws Exception {

        Unmarshaller unMarshaller = (pooling) ? getAvailableUnmarshaller() : createNewUnmarshaller();
        try {
            return unMarshaller.unmarshal(reader);
        } finally {
            if (pooling) {
                availableUnMarshallers.put(unMarshaller);
            }
        }
    }


    public Object unmarshal(XMLStreamReader reader) throws Exception {

        Unmarshaller unMarshaller = (pooling) ? getAvailableUnmarshaller() : createNewUnmarshaller();
        try {
            return unMarshaller.unmarshal(reader);
        } finally {
            if (pooling) {
                availableUnMarshallers.put(unMarshaller);
            }
        }
    }

    public boolean isPooling() {
        return pooling;
    }

    public void setPooling(boolean isPooling) {
        this.pooling = isPooling;
    }

    public boolean isEnableEventHandler() {
        return enableEventHandler;
    }

    public void setIsEnableEventHandler(boolean isEnableEventHandler) {
        enableEventHandler = isEnableEventHandler;
    }

    public void setMarshallerProperty(String key, Object value) {
        Assert.isTrue(availableMarshallers.isEmpty());
        if (value != null) {
            marshallerProperties.put(key, value);
        } else {
            marshallerProperties.remove(key);
        }
    }

    public void setMarshallerProperties(Map<String, Object> properties) {
        marshallerProperties.putAll(properties);
    }

    public void setUnmarshallerProperty(String key, Object value) {
        if (value == null) {
            unmarshallerProperties.put(key, value);
        } else {
            unmarshallerProperties.remove(key);
        }
    }

    public void setUnmarshallerProperty(Map<String, Object> properties) {
        assert availableUnMarshallers.isEmpty();
        unmarshallerProperties.putAll(properties);
    }

    private void waitForInitialization() {
        // make sure we are ready for business, wait otherwise
        if (State.OPERATIONAL != state) {
            synchronized (stateControl) {
                while (State.INITIALIZING == state) {
                    try {
                        stateControl.wait();
                    } catch (InterruptedException e) {
                        // ignore this, keep waiting
                    }
                }
                if (State.ERROR == state) {
                    throw new IllegalStateException(JAXB_SERIALIZER_IN_ERROR_STATE);
                }
            }
        }
    }

    private Marshaller createNewMarshaller() throws Exception {
        waitForInitialization();
        Marshaller marshaller = jaxbContext.createMarshaller();
        if (isEnableEventHandler()) {
            marshaller.setEventHandler(new ValidationEventHandlerImpl());
        }
        for (Map.Entry<String, Object> me : marshallerProperties.entrySet()) {
            marshaller.setProperty(me.getKey(), me.getValue());
        }
        return marshaller;
    }

    private Unmarshaller createNewUnmarshaller() throws Exception {
        waitForInitialization();
        Unmarshaller unMarshaller = jaxbContext.createUnmarshaller();
        if (isEnableEventHandler()) {
            unMarshaller.setEventHandler(new ValidationEventHandlerImpl());
        }
        for (Map.Entry<String, Object> me : unmarshallerProperties.entrySet()) {
            unMarshaller.setProperty(me.getKey(), me.getValue());
        }
        return unMarshaller;
    }

    private Unmarshaller getAvailableUnmarshaller() throws Exception {
        waitForInitialization();
        // get an unmarshaller from queue
        Unmarshaller unMarshaller = availableUnMarshallers.poll(); // NOPMD
        // blocking get if one is not available, create one if we can
        if (null == unMarshaller) {
            synchronized (availableUnMarshallers) {
                if (currentUnMarshallers < s_maxUnmarshallers) {
                    unMarshaller = createNewUnmarshaller();

                    if (null != unMarshaller) {
                        currentUnMarshallers++;
                    }
                }
            }
            // if still did not get one, wait for one to become available
            if (null == unMarshaller) {
                unMarshaller = availableUnMarshallers.take(); // blocking wait
            }
        }
        return unMarshaller;
    }

    private Marshaller getAvailableMarshaller() throws Exception {
        waitForInitialization();

        // get an unmarshaller from queue
        Marshaller marshaller = availableMarshallers.poll(); // NOPMDS
        // blocking get if one is not available, create one if we can
        if (null == marshaller) {
            synchronized (availableMarshallers) {
                if (currentMarshallers < s_maxMarshallers) {
                    marshaller = createNewMarshaller();
                    if (null != marshaller) {
                        currentMarshallers++;
                    }
                }
            }
            // if still did not get one, wait for one to become available
            if (null == marshaller) {
                marshaller = availableMarshallers.take(); // blocking wait
            }
        }
        return marshaller;
    }

    protected static void flushSerializers() throws Exception {
        Lock lock = READWRITE_LOCK.writeLock();
        lock.lock();
        try {
            REPOSITORY.clear();
        } finally {
            lock.unlock();
        }
    }

    public static int getMaxUnmarshallers() {
        return s_maxUnmarshallers;
    }

    public static void setMaxUnmarshallers(int unmarshallers) {
        if ((unmarshallers < 1) && (unmarshallers != -1)) {
            LOGGER.warn("Ignoring Invalid maximum marshallers requested (" + unmarshallers
                    + ") and using unbounded default, legal values are -1, 1,2,3,...");
            unmarshallers = Integer.MAX_VALUE;
        }
        LOGGER.info("Maximum marshallers config changed from (" + s_maxUnmarshallers + ") to (" + unmarshallers + ").");
        s_maxUnmarshallers = unmarshallers;
    }

    public static int getMaxMarshallers() {
        return s_maxMarshallers;
    }

    public static void setMaxMarshallers(int marshallers) {
        if ((marshallers < 1) && (marshallers != -1)) {
            LOGGER.warn("Ignoring Invalid maximum marshallers requested (" + marshallers
                    + ") and using unbounded default, legal values are -1, 1,2,3,...");
            marshallers = Integer.MAX_VALUE;
        }

        LOGGER.info("Maximum marshallers config changed from (" + s_maxMarshallers + ") to (" + marshallers + ").");
        s_maxMarshallers = marshallers;
    }

    public static int getMinUnmarshallers() {
        return s_minUnmarshallers;
    }

    public static void setMinUnmarshallers(int unmarshallers) {
        s_minUnmarshallers = unmarshallers;
    }

    public static int getMinMarshallers() {
        return s_minMarshallers;
    }

    public static void setMinMarshallers(int marshallers) {
        s_minMarshallers = marshallers;
    }

    private class ValidationEventHandlerImpl implements ValidationEventHandler {
        public boolean handleEvent(ValidationEvent ve) {
            ValidationEventLocator vel = ve.getLocator();
            StringBuilder errorMsg = new StringBuilder(128);
            if (null != vel) {
                errorMsg.append("Node: " + vel.getNode() + " Line:Col[" + vel.getLineNumber() + ":"
                        + vel.getColumnNumber() + "]:");
            }
            LOGGER.warn("Severity:" + ve.getSeverity() + " Error Message: " + errorMsg.toString() + ve.getMessage());
            return true;
        }
    }

}

*/