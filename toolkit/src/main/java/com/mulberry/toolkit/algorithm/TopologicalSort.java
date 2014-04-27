/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.toolkit.algorithm;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 4/27/14
 *         Time: 6:13 PM
 */
public class TopologicalSort {
    private Collection<Node> nodes;

    public TopologicalSort(Collection<Node> nodes) {
        super();
        this.nodes = nodes;
    }

    public List<Node> sort() {
        List<Node> removed = new LinkedList<Node>();
        Queue<Node> rootNodes = new LinkedList<Node>();

        offerRootNodes(nodes, removed, rootNodes);

        Node root;
        while ((root = rootNodes.poll()) != null) {
            removed.add(root);
            offerRootNodes(root.getDependentNodes(), removed, rootNodes);
        }
        return removed;
    }

    private void offerRootNodes(Collection<Node> nodesToCheck, Collection<Node> removedNodes, Queue<Node> rootNodes) {
        for (Node node : nodesToCheck) {
            if (removedNodes.containsAll(node.getNeededNodes())) {
                rootNodes.offer(node);
            }
        }
    }


    public static void main(String[] args) {

    }


    public static class Node {

        private final Collection<Node> neededNodes = new LinkedHashSet<Node>();
        private final Collection<Node> dependentNodes = new LinkedHashSet<Node>();
        private final String name;

        public Node(String name){
            this.name = name;
        }

        public Collection<Node> getDependentNodes() {
            return dependentNodes;
        }

        public Collection<?> getNeededNodes() {
            return neededNodes;
        }

        public String getName(){
            return name;
        }

        public void needs(Node node){
            neededNodes.add(node);
            node.dependentNodes.add(this);
        }

        public String toString(){
            return name;
        }
    }
}
