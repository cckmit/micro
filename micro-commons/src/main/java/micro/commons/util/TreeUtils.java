package micro.commons.util;

import micro.commons.annotation.ThreadSafe;
import micro.commons.tree.Node;

import java.util.*;
import java.util.stream.Collectors;


/**
 * 树形结构解析
 *
 * @author gewx
 **/

@ThreadSafe
public final class TreeUtils {

    /**
     * 解析数据结构
     *
     * @param nodeList 数据节点集合
     * @return 解析完成后的树状结果
     * @author gewx
     **/
    public static List<Node> parse(List<Node> nodeList) {
        boolean isDeadCycle = nodeList.stream().anyMatch(node -> node.getId().equals(node.getParentId()));
        if (isDeadCycle) {
            throw new RuntimeException("树形节点存在父子递归引用！");
        }

        List<Node> resultList = new ArrayList<>(64);
        nodeList.stream().filter(val -> !hasChild(nodeList, val.getId())).collect(Collectors.toList())
                .forEach(val -> reverseRecursion(val, nodeList, resultList));

        Set<Node> set = new HashSet<>(16);
        set.addAll(resultList);

        resultList.clear();
        set.stream().sorted(Comparator.comparing(Node::getSortNum)).forEach(val -> {
            resultList.add(val);
            recursion(val, nodeList);
        });

        return resultList;
    }

    /**
     * 取出某个节点直至末尾叶子节点的数据并解析为树状结构
     *
     * @param node     叶子节点
     * @param nodeList 数据节点集合
     * @author gewx
     **/
    public static void getNodeJson(Node node, List<Node> nodeList) {
        recursion(node, nodeList);
    }

    /**
     * 取出某个节点直至末尾叶子节点对象
     *
     * @param node       节点
     * @param nodeList   检索目标
     * @param resultList 结果
     * @author gewx
     **/
    public static void searchNodeDown(Node node, List<Node> nodeList, List<Node> resultList) {
        List<Node> childNodeList = nodeList.stream().filter(val -> val.getParentId().equals(node.getId())).distinct()
                .sorted(Comparator.comparing(Node::getSortNum)).collect(Collectors.toList());
        if (childNodeList.size() != 0) {
            resultList.addAll(childNodeList);
            childNodeList.forEach(val -> searchNodeDown(val, nodeList, resultList));
        }
    }

    /**
     * 取出某个节点直至末尾叶子节点对象
     *
     * @param nodeId     节点Id
     * @param nodeList   检索目标
     * @param resultList 结果
     * @author gewx
     **/
    public static void searchNodeDown(String nodeId, List<Node> nodeList, List<Node> resultList) {
        List<Node> childNodeList = nodeList.stream().filter(val -> val.getParentId().equals(nodeId)).distinct()
                .sorted(Comparator.comparing(Node::getSortNum)).collect(Collectors.toList());
        if (childNodeList.size() != 0) {
            resultList.addAll(childNodeList);
            childNodeList.forEach(val -> searchNodeDown(val.getId(), nodeList, resultList));
        }
    }

    /**
     * 取出某个节点直至根父节点对象
     *
     * @param node       节点
     * @param nodeList   检索目标
     * @param resultList 结果
     * @author gewx
     **/
    public static void searchNodeUp(Node node, List<Node> nodeList, List<Node> resultList) {
        List<Node> parentNodeList = nodeList.stream().filter(val -> val.getId().equals(node.getParentId()))
                .collect(Collectors.toList());
        if (parentNodeList.size() != 0) {
            Node parentNode = parentNodeList.get(0);
            resultList.add(parentNode);
            searchNodeUp(parentNode, nodeList, resultList);
        }
    }

    /**
     * 取出某个节点直至根父节点对象
     *
     * @param parentId   父节点Id
     * @param nodeList   检索目标
     * @param resultList 结果
     * @author gewx
     **/
    public static void searchNodeUp(String parentId, List<Node> nodeList, List<Node> resultList) {
        List<Node> parentNodeList = nodeList.stream().filter(val -> val.getId().equals(parentId))
                .collect(Collectors.toList());
        if (parentNodeList.size() != 0) {
            Node parentNode = parentNodeList.get(0);
            resultList.add(parentNode);
            searchNodeUp(parentNode.getParentId(), nodeList, resultList);
        }
    }

    /**
     * 检索树当中所有节点Id
     *
     * @param node      节点
     * @param arrayNode 目标节点集合
     * @author gewx
     **/
    public static void searchNodeId(Node node, List<String> arrayNode) {
        arrayNode.add(node.getId());
        if (!node.getChildren().isEmpty()) {
            node.getChildren().forEach(val -> {
                searchNodeId(val, arrayNode);
            });
        }
    }

    /**
     * 将树形结构转换为二维数据结构
     *
     * @param nodeList 数据节点集合
     * @return 解析完成后的树状结果
     * @author gewx
     **/
    public static List<Node> twoDimensionParse(List<Node> nodeList) {
        List<Node> resultList = new ArrayList<>(64);
        nodeList.forEach(node -> {
            resultList.add(node);
            twoDimensionRecursion(node.getId(), node, resultList);
            node.setChildren(Collections.emptyList());
        });
        return parse(resultList);
    }

    /**
     * 递归检索所有子节点,归纳至二节点
     *
     * @param nodeId     二级根节点id
     * @param node       数据节点
     * @param resultList 根节点集合
     * @author gewx
     **/
    private static void twoDimensionRecursion(String nodeId, Node node, List<Node> resultList) {
        node.getChildren().forEach(val -> {
            val.setParentId(nodeId);
            resultList.add(val);
            twoDimensionRecursion(nodeId, val, resultList);
            val.setChildren(Collections.emptyList());
        });
    }

    /**
     * 倒序递归检索所有父节点,由下往上直到所有根节点
     *
     * @param node       末尾叶子节点
     * @param nodeList   数据节点集合
     * @param resultList 根节点集合
     * @author gewx
     **/
    private static void reverseRecursion(Node node, List<Node> nodeList, List<Node> resultList) {
        List<Node> parentNodeList = nodeList.stream().filter(val -> val.getId().equals(node.getParentId()))
                .collect(Collectors.toList());
        if (parentNodeList.size() != 0) {
            Node parentNode = parentNodeList.get(0);
            reverseRecursion(parentNode, nodeList, resultList);
        } else {
            resultList.add(node);
        }
    }

    /**
     * 递归检索所有子节点,节点合并去重
     *
     * @param node     根节点
     * @param nodeList 数据节点集合
     * @author gewx
     **/
    private static void recursion(Node node, List<Node> nodeList) {
        List<Node> childNodeList = nodeList.stream().filter(val -> val.getParentId().equals(node.getId())).distinct()
                .sorted(Comparator.comparing(Node::getSortNum)).collect(Collectors.toList());
        if (childNodeList.size() != 0) {
            node.setChildren(childNodeList);
            childNodeList.forEach(val -> recursion(val, nodeList));
        }
    }

    /**
     * 是否还存在父级元素,找出末尾叶子节点
     *
     * @param node   叶子节点
     * @param menuId 节点标记
     * @return boolean true 存在, false 不存在
     * @author gewx
     **/
    private static boolean hasChild(List<Node> node, String menuId) {
        return node.stream().anyMatch(val -> val.getParentId().equals(menuId));
    }
}
