import java.util.Scanner;
class RedBlackTree {

    private static final boolean RED = true;
    private static final boolean BLACK = false;

    class Node {
        int data;
        Node left, right, parent;
        boolean color;

        Node(int data) {
            this.data = data;
            this.color = RED;
        }
    }

    private Node root;

    private void rotateLeft(Node node) {
        Node right = node.right;
        node.right = right.left;
        if (right.left != null) {
            right.left.parent = node;
        }
        right.parent = node.parent;
        if (node.parent == null) {
            root = right;
        } else if (node == node.parent.left) {
            node.parent.left = right;
        } else {
            node.parent.right = right;
        }
        right.left = node;
        node.parent = right;
    }

    private void rotateRight(Node node) {
        Node left = node.left;
        node.left = left.right;
        if (left.right != null) {
            left.right.parent = node;
        }
        left.parent = node.parent;
        if (node.parent == null) {
            root = left;
        } else if (node == node.parent.right) {
            node.parent.right = left;
        } else {
            node.parent.left = left;
        }
        left.right = node;
        node.parent = left;
    }

    private void fixInsert(Node node) {
        Node parent, grandparent;
        while (node != root && node.parent.color == RED) {
            parent = node.parent;
            grandparent = parent.parent;
            if (parent == grandparent.left) {
                Node uncle = grandparent.right;
                if (uncle != null && uncle.color == RED) {
                    grandparent.color = RED;
                    parent.color = BLACK;
                    uncle.color = BLACK;
                    node = grandparent;
                } else {
                    if (node == parent.right) {
                        rotateLeft(parent);
                        node = parent;
                        parent = node.parent;
                    }
                    rotateRight(grandparent);
                    boolean tmp = parent.color;
                    parent.color = grandparent.color;
                    grandparent.color = tmp;
                    node = parent;
                }
            } else {
                Node uncle = grandparent.left;
                if (uncle != null && uncle.color == RED) {
                    grandparent.color = RED;
                    parent.color = BLACK;
                    uncle.color = BLACK;
                    node = grandparent;
                } else {
                    if (node == parent.left) {
                        rotateRight(parent);
                        node = parent;
                        parent = node.parent;
                    }
                    rotateLeft(grandparent);
                    boolean tmp = parent.color;
                    parent.color = grandparent.color;
                    grandparent.color = tmp;
                    node = parent;
                }
            }
        }
        root.color = BLACK;
    }

    public void insert(int data) {
        Node node = new Node(data);
        if (root == null) {
            root = node;
            root.color = BLACK;
            return;
        }

        Node temp = root;
        Node parent = null;
        while (temp != null) {
            parent = temp;
            if (node.data < temp.data) {
                temp = temp.left;
            } else if (node.data > temp.data) {
                temp = temp.right;
            } else {
                return;
            }
        }
        node.parent = parent;
        if (node.data < parent.data) {
            parent.left = node;
        } else {
            parent.right = node;
        }

        fixInsert(node);
    }

    public Node search(int data) {
        Node temp = root;
        while (temp != null) {
            if (data < temp.data) {
                temp = temp.left;
            } else if (data > temp.data) {
                temp = temp.right;
            } else {
                return temp;
            }
        }
        return null;
    }

    private void fixDelete(Node node) {
        while (node != root && node.color == BLACK) {
            if (node == node.parent.left) {
                Node sibling = node.parent.right;
                if (sibling.color == RED) {
                    sibling.color = BLACK;
                    node.parent.color = RED;
                    rotateLeft(node.parent);
                    sibling = node.parent.right;
                }
                if ((sibling.left == null || sibling.left.color == BLACK) &&
                        (sibling.right == null || sibling.right.color == BLACK)) {
                    sibling.color = RED;
                    node = node.parent;
                } else {
                    if (sibling.right == null || sibling.right.color == BLACK) {
                        sibling.left.color = BLACK;
                        sibling.color = RED;
                        rotateRight(sibling);
                        sibling = node.parent.right;
                    }
                    sibling.color = node.parent.color;
                    node.parent.color = BLACK;
                    if (sibling.right != null) {
                        sibling.right.color = BLACK;
                    }
                    rotateLeft(node.parent);
                    node = root;
                }
            } else {
                Node sibling = node.parent.left;
                if (sibling.color == RED) {
                    sibling.color = BLACK;
                    node.parent.color = RED;
                    rotateRight(node.parent);
                    sibling = node.parent.left;
                }
                if ((sibling.left == null || sibling.left.color == BLACK) &&
                        (sibling.right == null || sibling.right.color == BLACK)) {
                    sibling.color = RED;
                    node = node.parent;
                } else {
                    if (sibling.left == null || sibling.left.color == BLACK) {
                        sibling.right.color = BLACK;
                        sibling.color = RED;
                        rotateLeft(sibling);
                        sibling = node.parent.left;
                    }
                    sibling.color = node.parent.color;
                    node.parent.color = BLACK;
                    if (sibling.left != null) {
                        sibling.left.color = BLACK;
                    }
                    rotateRight(node.parent);
                    node = root;
                }
            }
        }
        node.color = BLACK;
    }

    public void delete(int data) {
        Node node = search(data);
        if (node == null) {
            return;
        }

        Node y = node;
        Node x;
        boolean yOriginalColor = y.color;

        if (node.left == null) {
            x = node.right;
            transplant(node, node.right);
        } else if (node.right == null) {
            x = node.left;
            transplant(node, node.left);
        } else {
            y = minimum(node.right);
            yOriginalColor = y.color;
            x = y.right;
            if (y.parent == node) {
                if (x != null) {
                    x.parent = y;
                }
            } else {
                transplant(y, y.right);
                y.right = node.right;
                y.right.parent = y;
            }
            transplant(node, y);
            y.left = node.left;
            y.left.parent = y;
            y.color = node.color;
        }

        if (yOriginalColor == BLACK) {
            fixDelete(x);
        }
    }

    private void transplant(Node target, Node with) {
        if (target.parent == null) {
            root = with;
        } else if (target == target.parent.left) {
            target.parent.left = with;
        } else {
            target.parent.right = with;
        }
        if (with != null) {
            with.parent = target.parent;
        }
    }

    private Node minimum(Node node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    public void printTree(Node node, String indent, boolean last) {
        if (node != null) {
            System.out.print(indent);
            if (last) {
                System.out.print("R----");
                indent += "   ";
            } else {
                System.out.print("L----");
                indent += "|  ";
            }

            String sColor = node.color == RED ? "RED" : "BLACK";
            System.out.println(node.data + "(" + sColor + ")");
            printTree(node.left, indent, false);
            printTree(node.right, indent, true);
        }
    }

    public Node getRoot() {
        return root;
    }
}


public class RedBlackTreeMain {

    public static void main(String[] args) {
        RedBlackTree tree = new RedBlackTree();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Red-Black Tree Operations:");
            System.out.println("1. Insert");
            System.out.println("2. Delete");
            System.out.println("3. Search");
            System.out.println("4. Print Tree");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.print("Enter value to insert: ");
                    int insertValue = scanner.nextInt();
                    tree.insert(insertValue);
                    System.out.println("Value inserted.");
                    break;
                case 2:
                    System.out.print("Enter value to delete: ");
                    int deleteValue = scanner.nextInt();
                    tree.delete(deleteValue);
                    System.out.println("Value deleted.");
                    break;
                case 3:
                    System.out.print("Enter value to search: ");
                    int searchValue = scanner.nextInt();
                    RedBlackTree.Node node = tree.search(searchValue);
                    if (node != null) {
                        System.out.println("Value found.");
                    } else {
                        System.out.println("Value not found.");
                    }
                    break;
                case 4:
                    System.out.println("Red-Black Tree:");
                    tree.printTree(tree.getRoot(), "", true);
                    break;
                case 5:
                    System.out.println("Exiting...");
                    scanner.close();
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
