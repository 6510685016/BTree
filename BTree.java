package BTree;

public class BTree {
    Node root; // Root of the B-tree
    int t; // Minimum degree

    // Constructor
    public BTree(int t) {
        this.root = null;
        this.t = t;
    }

    // Function to write node to disk
    public void DISK_WRITE(Node node) {
        // Implementation goes here
    }

    // Function to free node from disk
    public void DISK_FREE(Node node) {
        // Implementation goes here
    }

    // Other methods for B-tree operations (such as insertion, search, etc.) would
    // go here

    // Function to delete key k from B-Tree T
    public void delete(int k) {
        Node r = root;
        B_TREE_DEL_NONMIN(r, k);
        if (r.n == 0) {
            Node y = r;
            root = r.c[0];
            DISK_FREE(y);
        }
    }

    // Function to perform non-minimal delete operation
    public void B_TREE_DEL_NONMIN(Node x, int k) {
        int i = 0;
        while (i < x.n && k > x.key[i]) {
            i++;
        }
        if (i < x.n && k == x.key[i] && x.leaf) {
            B_TREE_KEYDEL(x, i);
            DISK_WRITE(x);
            return;
        }
        if (i < x.n && k == x.key[i] && !x.leaf) {
            Node child = x.c[i];
            if (child.n > t - 1) {
                B_TREE_DEL_PRED(x, i, child);
                return;
            }
            Node nextChild = x.c[i + 1];
            if (nextChild.n > t - 1) {
                B_TREE_DEL_SUCC(x, i, nextChild);
                return;
            }
            B_TREE_JOIN_NODES(x, i, child, nextChild);
            B_TREE_DEL_NONMIN(child, k);
            return;
        }
        if (!x.leaf) {
            Node child = x.c[i];
            if (child.n == t - 1 && i > 0) {
                Node leftSibling = x.c[i - 1];
                if (leftSibling.n > t - 1) {
                    B_TREE_KEY_FROM_LEFT(x, i - 1, leftSibling, child);
                    B_TREE_DEL_NONMIN(child, k);
                    return;
                }
            }
            if (child.n == t - 1 && i < x.n) {
                Node rightSibling = x.c[i + 1];
                if (rightSibling.n > t - 1) {
                    B_TREE_KEY_FROM_RIGHT(x, i, child, rightSibling);
                    B_TREE_DEL_NONMIN(child, k);
                    return;
                }
            }
            if (child.n == t - 1 && i > 0) {
                B_TREE_JOIN_NODES(x, i - 1, x.c[i - 1], child);
                i--;
            } else if (child.n == t - 1 && i < x.n) {
                B_TREE_JOIN_NODES(x, i, child, x.c[i + 1]);
            }
            B_TREE_DEL_NONMIN(x.c[i], k);
            return;
        }
    }

    // Function to delete key i from leaf node x
    public void B_TREE_KEYDEL(Node x, int i) {
        for (int j = i; j < x.n - 1; j++) {
            x.key[j] = x.key[j + 1];
        }
        x.n--;
        DISK_WRITE(x);
    }

    // Function to join nodes
    public void B_TREE_JOIN_NODES(Node x, int i, Node y, Node z) {
        y.key[t - 1] = x.key[i];
        for (int j = 0; j < t - 1; j++) {
            y.key[t + j] = z.key[j];
        }
        if (!y.leaf) {
            for (int j = 0; j < t; j++) {
                y.c[t + j] = z.c[j];
            }
        }
        y.n = 2 * t - 1;
        for (int j = i; j < x.n - 1; j++) {
            x.key[j] = x.key[j + 1];
        }
        for (int j = i + 1; j < x.n; j++) {
            x.c[j] = x.c[j + 1];
        }
        x.n--;
        DISK_FREE(z);
        DISK_WRITE(x);
        DISK_WRITE(y);
    }

    // Function to delete predecessor
    public void B_TREE_DEL_PRED(Node x, int i, Node y) {
        if (y.leaf) {
            x.key[i] = y.key[y.n - 1];
            B_TREE_KEYDEL(y, y.n - 1);
            DISK_WRITE(x);
            DISK_WRITE(y);
            return;
        }
        if (y.c[y.n].n > t - 1) {
            B_TREE_DEL_PRED(x, i, y.c[y.n]);
            return;
        }
        if (y.c[y.n + 1].n > t - 1) {
            B_TREE_KEY_FROM_LEFT(y, y.n, y.c[y.n], y.c[y.n + 1]);
            B_TREE_DEL_PRED(x, i, y.c[y.n + 1]);
            return;
        }
        B_TREE_JOIN_NODES(y, y.n, y.c[y.n], y.c[y.n + 1]);
        B_TREE_DEL_PRED(x, i, y.c[y.n + 1]);
    }

    // Function to delete successor
    public void B_TREE_DEL_SUCC(Node x, int i, Node y) {
        if (y.leaf) {
            x.key[i] = y.key[0];
            B_TREE_KEYDEL(y, 0);
            DISK_WRITE(x);
            DISK_WRITE(y);
            return;
        }
        if (y.c[0].n > t - 1) {
            B_TREE_DEL_SUCC(x, i, y.c[0]);
            return;
        }
        if (y.c[1].n > t - 1) {
            B_TREE_KEY_FROM_RIGHT(y, 0, y.c[0], y.c[1]);
            B_TREE_DEL_SUCC(x, i, y.c[1]);
            return;
        }
        B_TREE_JOIN_NODES(y, 0, y.c[0], y.c[1]);
        B_TREE_DEL_SUCC(x, i, y.c[1]);
    }

    public void B_TREE_KEY_FROM_LEFT(Node x, int i, Node y, Node z) {
        for (int j = z.n - 1; j >= 0; j--) {
            z.key[j + 1] = z.key[j];
        }
        if (!z.leaf) {
            for (int j = z.n; j >= 0; j--) {
                z.c[j + 1] = z.c[j];
            }
        }
        z.key[0] = x.key[i];
        x.key[i] = y.key[y.n - 1];
        if (!y.leaf) {
            z.c[0] = y.c[y.n];
        }
        y.n--;
        z.n++;
        DISK_WRITE(x);
        DISK_WRITE(y);
        DISK_WRITE(z);
    }

    public void B_TREE_KEY_FROM_RIGHT(Node x, int i, Node y, Node z) {
        y.key[y.n] = x.key[i];
        x.key[i] = z.key[0];

        if (!y.leaf) {
            y.c[y.n] = z.c[0];
        }
        y.n++;

        for (int j = 0; j < z.n - 1; j++) {
            z.key[j] = z.key[j + 1];
        }
        if (!z.leaf) {
            for (int j = 0; j < z.n; j++) {
                z.c[j] = z.c[j + 1];
            }
        }
        z.n--;
        DISK_WRITE(x);
        DISK_WRITE(y);
        DISK_WRITE(z);
    }

    public void traverse() {
        if (root != null) {
            root.traverse();
        }
    }

    public void insert(int key) {
        // If tree is empty, create a new root node
        if (root == null) {
            root = new Node(t, true);
            root.key[0] = key;
            root.n = 1;
        } else { // If tree is not empty
            // If root is full, split it and increase tree height
            if (root.n == 2 * t - 1) {
                Node newNode = new Node(t, false);
                newNode.c[0] = root;
                splitChild(newNode, 0, root);
                int i = 0;
                if (newNode.key[0] < key) {
                    i++;
                }
                insertNonFull(newNode.c[i], key);
                root = newNode;
            } else { // If root is not full, insert key into root
                insertNonFull(root, key);
            }
        }
    }

    // Helper function to insert a key into a non-full node
    private void insertNonFull(Node node, int key) {
        int i = node.n - 1;
        if (node.leaf) {
            // Shift all greater keys one place ahead
            while (i >= 0 && node.key[i] > key) {
                node.key[i + 1] = node.key[i];
                i--;
            }
            // Insert the new key
            node.key[i + 1] = key;
            node.n++;
        } else { // If node is not a leaf
            // Find child which is going to have the new key
            while (i >= 0 && node.key[i] > key) {
                i--;
            }
            i++;
            // If the child is full, split it
            if (node.c[i].n == 2 * t - 1) {
                splitChild(node, i, node.c[i]);
                // Determine which of the two children is now the correct one to descend to
                if (node.key[i] < key) {
                    i++;
                }
            }
            // Recursive call to insert the key into the appropriate child
            insertNonFull(node.c[i], key);
        }
    }

    // Helper function to split a full child of node
    private void splitChild(Node parentNode, int childIndex, Node childNode) {
        Node newNode = new Node(t, childNode.leaf);
        newNode.n = t - 1;
        // Copy the last t - 1 keys of childNode to newNode
        for (int j = 0; j < t - 1; j++) {
            newNode.key[j] = childNode.key[j + t];
        }
        // Copy the last t children of childNode to newNode if it's not a leaf
        if (!childNode.leaf) {
            for (int j = 0; j < t; j++) {
                newNode.c[j] = childNode.c[j + t];
            }
        }
        // Reduce the number of keys in childNode
        childNode.n = t - 1;
        // Shift the keys and children of parentNode to make room for the new child
        for (int j = parentNode.n; j >= childIndex + 1; j--) {
            parentNode.c[j + 1] = parentNode.c[j];
        }
        parentNode.c[childIndex + 1] = newNode;
        for (int j = parentNode.n - 1; j >= childIndex; j--) {
            parentNode.key[j + 1] = parentNode.key[j];
        }
        // Move the median key of childNode to parentNode
        parentNode.key[childIndex] = childNode.key[t - 1];
        parentNode.n++;
    }

    public boolean search(int key) {
        return (root != null) && root.search(key);
    }

    public static void main(String[] args) {
        // Create a B-tree with minimum degree 3
        BTree bTree = new BTree(3);

        // Insert keys into the B-tree
        int[] keysToInsert = { 32, 98, 40, 86, 75, 57, 24, 71, 64, 79, 16, 67, 26, 93, 37, 68, 49, 62, 11, 84, 30, 7 };
        for (int key : keysToInsert) {
            bTree.insert(key);
        }

        bTree.traverse();
        System.out.println();

        // Delete keys from the B-tree
        int[] keysToDelete = { 32, 98, 40, 86, 75, 57, 24, 71, 64, 79, 16, 67, 26, 93, 37, 68, 49, 62, 11, 84, 30, 7 };
        for (int key : keysToDelete) {
            bTree.delete(key);
            System.out.println("After deleting key " + key + ":");
            bTree.traverse();
            System.out.println();
        }
    }

}
