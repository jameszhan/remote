package com.mulberry.athena.common.io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by IntelliJ IDEA.
 * User: James
 * Date: 11-12-28
 * Time: 下午5:39
 * To change this template use File | Settings | File Templates.
 */
public class Server {

    private Selector selector;
    private ConcurrentMap<SocketChannel, Handle> map = new ConcurrentHashMap<SocketChannel, Handle>();

    public static void main(String[] args) throws IOException {

    }

    public void start() throws IOException{
        Selector selector = Selector.open();
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.socket().bind(new InetSocketAddress(1234));
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);


        for(;;) {
            selector.select();
            Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                if (key.isValid())
                    handle(key);
                keyIterator.remove();
            }
        }
    }

    public void handle(SelectionKey key) throws IOException {
        if (key.isAcceptable()) {
            ServerSocketChannel channel = (ServerSocketChannel) key.channel();
            SocketChannel socketChannel = channel.accept();
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);
            map.put(socketChannel, new Handle());
        }

        if (key.isReadable() || key.isWritable()) {
            SocketChannel socketChannel = (SocketChannel) key.channel();
            final Handle handle = map.get(socketChannel);
            if (handle != null) {
                handle.handle(key);
            }
        }
    }


    private class Handle {
        private StringBuilder message;
        private boolean writeOK = true;
        private ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        private FileChannel fileChannel;
        private String fileName;

        public void handle(SelectionKey key) throws IOException {
            if (key.isReadable()) {
                SocketChannel socketChannel = (SocketChannel) key.channel();
                if (writeOK)
                    message = new StringBuilder();
                while (true) {
                    byteBuffer.clear();
                    int r = socketChannel.read(byteBuffer);
                    if (r == 0)
                        break;
                    if (r == -1) {
                        socketChannel.close();
                        key.cancel();
                        return;
                    }
                    message.append(new String(byteBuffer.array(), 0, r));
                }

               // if (writeOK && invokeMessage(message)) {
                 //   socketChannel.register(selector, SelectionKey.OP_WRITE);
                   // writeOK = false;
                //}
            }
        }

    }
}
