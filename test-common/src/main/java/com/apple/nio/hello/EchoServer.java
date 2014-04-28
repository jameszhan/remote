package com.apple.nio.hello;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: James
 * Date: 11-12-28
 * Time: 下午5:58
 * To change this template use File | Settings | File Templates.
 */
public class EchoServer {

    private Selector selector;

    public void start() throws IOException {
        selector = Selector.open();

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.socket().bind(new InetSocketAddress(11111));
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        for (;;) {
            int count = selector.select();
            if(count > 0){
                Iterator<SelectionKey> i = selector.selectedKeys().iterator();
                while (i.hasNext()) {
                    SelectionKey key = i.next();
                    if(key.isValid()){
                        handle(key);
                    }
                    i.remove();
                }
            }else{
                debug("Select finished without any keys.");
            }

        }

    }

    public void handle(SelectionKey key) throws IOException {
        if(key.isAcceptable()){
            debug(key);
            SocketChannel sc = ((ServerSocketChannel)key.channel()).accept();
            sc.configureBlocking(false);
            sc.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        }else if(key.isReadable()){
            debug(key);
            SocketChannel sc = (SocketChannel)key.channel();
            ByteBuffer buf = ByteBuffer.allocate(1024);
            int len = sc.read(buf);
            if(len > 0){
                debug(new String(buf.array(), 0, len));
            }
        }
    }


    public static void debug(String msg){
        System.out.println(msg);
    }

    public static void debug(SelectionKey sk) {

        String s = new String();
        s = "Att: " + (sk.attachment() == null ? "no" : "yes");
        s += ", Read: " + sk.isReadable();
        s += ", Acpt: " + sk.isAcceptable();
        s += ", Cnct: " + sk.isConnectable();
        s += ", Wrt: " + sk.isWritable();
        s += ", Valid: " + sk.isValid();
        s += ", Ops: " + sk.interestOps();
        debug(s);

    }

    public static void main(String[] args) throws IOException {
        new EchoServer().start();
    }

}
