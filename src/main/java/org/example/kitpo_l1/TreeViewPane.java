package org.example.kitpo_l1;

import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.*;

/**
 * Простейший визуализатор бинарного дерева на Pane.
 */
public class TreeViewPane extends Pane {

    private static final int NODE_RADIUS = 18;
    private static final int H_GAP = 70;
    private static final int V_GAP = 80;
    private static final int MARGIN = 20;
    private static final int MAX_LABEL_LEN = 10;

    public TreeViewPane() {
        // нужен публичный конструктор без аргументов для FXMLLoader
        setPrefSize(600, 400);
    }

    public static class HitInfo {
        public final Node node;
        public final int x;
        public final int y;
        public HitInfo(Node node, int x, int y) {
            this.node = node; this.x = x; this.y = y;
        }
    }

    public void clear() {
        getChildren().clear();
    }

    public HitInfo hitTest(int x, int y) {
        for (javafx.scene.Node n : getChildren()) {
            if (n instanceof Group) {
                Group g = (Group) n;
                for (javafx.scene.Node ch : g.getChildren()) {
                    if (ch instanceof Circle) {
                        Circle c = (Circle) ch;
                        double cx = c.getCenterX();
                        double cy = c.getCenterY();
                        double r = c.getRadius();
                        double dx = x - cx;
                        double dy = y - cy;
                        if (dx * dx + dy * dy <= r * r) {
                            Object ud = g.getUserData();
                            if (ud instanceof Node) {
                                return new HitInfo((Node) ud, (int) cx, (int) cy);
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public void drawTree(BinaryTree tree) {
        getChildren().clear();
        if (tree == null || tree.getRoot() == null) {
            Label l = new Label("Пусто");
            l.setLayoutX(10);
            l.setLayoutY(10);
            getChildren().add(l);
            return;
        }

        List<Node> inorder = new ArrayList<>();
        traverseInOrder(tree.getRoot(), inorder);

        Map<Node, Integer> xIndex = new HashMap<>();
        for (int i = 0; i < inorder.size(); i++) xIndex.put(inorder.get(i), i);

        Map<Node, Integer> depth = new HashMap<>();
        computeDepths(tree.getRoot(), 0, depth);

        Map<Node, Double> coordX = new HashMap<>();
        Map<Node, Double> coordY = new HashMap<>();
        for (Node n : inorder) {
            int xi = xIndex.get(n);
            int d = depth.getOrDefault(n, 0);
            double x = MARGIN + xi * H_GAP;
            double y = MARGIN + d * V_GAP;
            coordX.put(n, x);
            coordY.put(n, y);
        }

        // ребра
        for (Node n : inorder) {
            double x = coordX.get(n);
            double y = coordY.get(n);
            if (n.left != null) {
                double x2 = coordX.get(n.left);
                double y2 = coordY.get(n.left);
                Line line = new Line(x, y + NODE_RADIUS, x2, y2 - NODE_RADIUS);
                line.setStroke(Color.GRAY);
                getChildren().add(line);
            }
            if (n.right != null) {
                double x2 = coordX.get(n.right);
                double y2 = coordY.get(n.right);
                Line line = new Line(x, y + NODE_RADIUS, x2, y2 - NODE_RADIUS);
                line.setStroke(Color.GRAY);
                getChildren().add(line);
            }
        }

        // узлы
        for (Node n : inorder) {
            double x = coordX.get(n);
            double y = coordY.get(n);

            Group g = new Group();
            g.setUserData(n);

            Circle c = new Circle(x, y, NODE_RADIUS);
            c.setStroke(Color.BLACK);
            c.setFill(Color.web("#f8f8ff"));
            g.getChildren().add(c);

            String txt = n.toString();
            if (txt.length() > MAX_LABEL_LEN) txt = txt.substring(0, MAX_LABEL_LEN - 1) + "…";

            Text t = new Text(x, y + 4, txt);
            t.setTextOrigin(VPos.CENTER);
            t.setFont(Font.font(11));
            double textWidth = t.getLayoutBounds().getWidth();
            t.setX(x - textWidth / 2);
            g.getChildren().add(t);

            getChildren().add(g);
        }

        double prefW = MARGIN * 2 + inorder.size() * H_GAP;
        double maxDepth = depth.values().stream().mapToInt(Integer::intValue).max().orElse(0);
        double prefH = MARGIN * 2 + (maxDepth + 1) * V_GAP;
        setPrefSize(Math.max(getPrefWidth(), prefW), Math.max(getPrefHeight(), prefH));
    }

    private void traverseInOrder(Node n, List<Node> out) {
        if (n == null) return;
        traverseInOrder(n.left, out);
        out.add(n);
        traverseInOrder(n.right, out);
    }

    private void computeDepths(Node n, int d, Map<Node, Integer> depth) {
        if (n == null) return;
        depth.put(n, d);
        computeDepths(n.left, d + 1, depth);
        computeDepths(n.right, d + 1, depth);
    }
}
