package BTree;

public class Node {
    int[] key; // Array to store keys
    int n; // Number of keys currently stored in the node
    Node[] c; // Array to store child pointers
    boolean leaf; // True if node is a leaf, false otherwise

    // Constructor
    public Node(int t, boolean leaf) {
        this.key = new int[2 * t];
        this.n = 0;
        this.c = new Node[2 * t + 1];
        this.leaf = leaf;
    }

    // Function to read node from disk
    public static Node DISK_READ(int offset) {
        // Placeholder implementation
        System.out.println("Reading node from disk at offset: " + offset);
        return null; // Placeholder
    }

    // Function to write node to disk
    public static void DISK_WRITE(Node node) {
        // Placeholder implementation
        System.out.println("Writing node to disk");
    }

    public void traverse() {
        // Traverse all keys and children recursively
        int i;
        for (i = 0; i < n; i++) {
            // If this is not a leaf node, traverse its children first
            if (!leaf) {
                c[i].traverse();
            }
            // Print the key
            System.out.print(" " + leaf + " " + key[i]);
        }

        // Print the subtree rooted with last child
        if (!leaf) {
            c[i].traverse();
        }
    }

    public boolean search(int key) {
        int i = 0;
        while (i < n && key > this.key[i]) {
            i++;
        }
        if (i < n && key == this.key[i]) {
            return true; // Key found in this node
        }
        if (leaf) {
            return false; // Key not found and this node is a leaf
        }
        return c[i].search(key); // Recursively search in the appropriate child node
    }
}
