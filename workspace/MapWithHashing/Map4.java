import java.util.Iterator;
import java.util.NoSuchElementException;

import components.array.Array;
import components.array.Array1L;
import components.map.Map;
import components.map.Map2;
import components.map.MapSecondary;

/**
 * {@code Map} represented as a hash table using {@code Map}s for the buckets,
 * with implementations of primary methods.
 * 
 * @param <K>
 *            type of {@code Map} domain (key) entries
 * @param <V>
 *            type of {@code Map} range (associated value) entries
 * @convention <pre>
 * {@code |$this.hashTable.entries| > 0  and
 * for all i: integer, pf: PARTIAL_FUNCTION, x: K
 *     where (0 <= i  and  i < |$this.hashTable.entries|  and
 *            <pf> = $this.hashTable.entries[i, i+1)]  and
 *            x is in DOMAIN(pf))
 *   ([computed result of x.hashCode()] mod |$this.hashTable.entries| = i))  and
 * |$this.hashTable.examinableIndices| = |$this.hashTable.entries|  and
 * $this.size = sum i: integer, pf: PARTIAL_FUNCTION
 *     where (0 <= i  and  i < |$this.hashTable.entries|  and
 *            <pf> = $this.hashTable.entries[i, i+1))
 *   (|pf|)}
 * </pre>
 * @correspondence <pre>
 * {@code this = union i: integer, pf: PARTIAL_FUNCTION
 *            where (0 <= i  and  i < |$this.hashTable.entries|  and
 *                   <pf> = $this.hashTable[i, i+1))
 *          (pf)}
 * </pre>
 */
public class Map4<K, V> extends MapSecondary<K, V> {

    /*
     * Private members --------------------------------------------------------
     */

    /**
     * Default size of hash table.
     */
    private static final int DEFAULT_HASH_TABLE_SIZE = 100;

    /**
     * Buckets for hashing.
     */
    private Array<Map<K, V>> hashTable;

    /**
     * Total size of abstract {@code this}.
     */
    private int size;

    /**
     * Computes {@code a} mod {@code b} as % should have been defined to work.
     * 
     * @param a
     *            the number being reduced
     * @param b
     *            the modulus
     * @return the result of a mod b, which satisfies 0 <= {@code mod} < b
     * @requires <pre>
     * {@code b > 0}
     * </pre>
     * @ensures <pre>
     * {@code 0 <= mod  and  mod < b  and
     * there exists k: integer (a = k * b + mod)}
     * </pre>
     */
    private static int mod(int a, int b) {
        assert b > 0 : "Violation of: b > 0";
        return ((a % b) + b) % b;
    }

    /**
     * Creator of initial representation.
     * 
     * @param hashTableSize
     *            the size of the hash table
     * @requires <pre>
     * {@code hashTableSize > 0}
     * </pre>
     * @ensures <pre>
     * {@code |$this.hashTable.entries| = hashTableSize  and
     * for all i: integer
     *     where (0 <= i  and  i < |$this.hashTable.entries|)
     *   ($this.hashTable.entries[i, i+1) = <{}>  and
     *    i is in $this.hashTable.examinableIndices)  and
     * $this.size = 0
     * </pre>
     */
    private void createNewRep(int hashTableSize) {
        //Size begins as 0 and hash table is created as a new array of hash 
        //table size then each bucket is set as a new Map2 implementation
        this.size = 0;
        this.hashTable = new Array1L<>(hashTableSize);
        for (int i = 0; i < this.hashTable.length(); i++) {
            this.hashTable.setEntry(i, new Map2<K, V>());
        }
    }

    /*
     * Constructors -----------------------------------------------------------
     */

    /**
     * Default constructor.
     */
    public Map4() {
        //Create rep of a default hash table size of 100 buckets
        this.createNewRep(DEFAULT_HASH_TABLE_SIZE);
    }

    /**
     * Constructor resulting in a hash table of size {@code hashTableSize}.
     * 
     * @param hashTableSize
     *            size of hash table
     * @requires <pre>
     * {@code hashTableSize > 0}
     * </pre>
     * @ensures <pre>
     * {@code this = {}}
     * </pre>
     */
    public Map4(int hashTableSize) {
        //Create rep of a given hash table size
        this.createNewRep(hashTableSize);
    }

    /*
     * Standard methods -------------------------------------------------------
     */

    @SuppressWarnings("unchecked")
    @Override
    public final Map<K, V> newInstance() {
        try {
            return this.getClass().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new AssertionError("Cannot construct object of type "
                    + this.getClass());
        }
    }

    @Override
    public final void clear() {
        this.createNewRep(DEFAULT_HASH_TABLE_SIZE);
    }

    @Override
    public final void transferFrom(Map<K, V> source) {
        assert source != null : "Violation of: source is not null";
        assert source != this : "Violation of: source is not this";
        assert source instanceof Map4<?, ?> : ""
                + "Violation of: source is of dynamic type Map4<?,?>";
        /*
         * This cast cannot fail since the assert above would have stopped
         * execution in that case: source must be of dynamic type Map4<?,?>, and
         * the ?,? must be K,V or the call would not have compiled.
         */
        Map4<K, V> localSource = (Map4<K, V>) source;
        this.hashTable = localSource.hashTable;
        this.size = localSource.size;
        localSource.createNewRep(DEFAULT_HASH_TABLE_SIZE);
    }

    /*
     * Kernel methods ---------------------------------------------------------
     */

    @Override
    public final void add(K key, V value) {
        assert key != null : "Violation of: key is not null";
        assert value != null : "Violation of: value is not null";
        assert !this.hasKey(key) : "Vi"
                + "olation of: key is not in DOMAIN(this)";
        //Find the correct bucket, then add it to that bucket in the array 
        //and increase size of the map by 1
        this.hashTable.entry(mod(key.hashCode(), this.hashTable.length())).add(
                key, value);
        this.size++;
    }

    @Override
    public final Pair<K, V> remove(K key) {
        assert key != null : "Violation of: key is not null";
        assert this.hasKey(key) : "Violation of: key is in DOMAIN(this)";
        //Find the correct bucket, then remove it from that bucket in the array
        //and decrease size of the map by 1
        Pair<K, V> p = this.hashTable.entry(
                mod(key.hashCode(), this.hashTable.length())).remove(key);
        this.size--;
        return p;
    }

    @Override
    public final Pair<K, V> removeAny() {
        assert this.size() > 0 : "Violation of: this /= empty_set";
        //Check each entry of the array while the size of the entry at i isnt 
        //0 and i is less than the length then when that is found remove 
        //any pair from that bucket in the array
        int i = 0;
        while (i < this.hashTable.length()
                && !(this.hashTable.entry(i).size() > 0)) {
            i++;
        }
        Pair<K, V> p = this.hashTable.entry(i).removeAny();
        this.size--;
        return p;
    }

    @Override
    public final V value(K key) {
        assert key != null : "Violation of: key is not null";
        assert this.hasKey(key) : "Violation of: key is in DOMAIN(this)";
        //Find the correct bucket, then get the value associated with the key
        return this.hashTable.entry(
                mod(key.hashCode(), this.hashTable.length())).value(key);
    }

    @Override
    public final boolean hasKey(K key) {
        assert key != null : "Violation of: key is not null";
        //Find the correct bucket, then check if the key is in that bucket 
        //of the array
        return this.hashTable.entry(
                mod(key.hashCode(), this.hashTable.length())).hasKey(key);
    }

    @Override
    public final int size() {
        //Return the size of the map
        return this.size;
    }

    @Override
    public final Iterator<Pair<K, V>> iterator() {
        return new Map4Iterator();
    }

    /**
     * Implementation of {@code Iterator} interface for {@code Map4}.
     */
    private final class Map4Iterator implements Iterator<Pair<K, V>> {

        /**
         * Number of elements seen already (i.e., |~this.seen|).
         */
        private int numberSeen;

        /**
         * Bucket from which current bucket iterator comes.
         */
        private int currentBucket;

        /**
         * Bucket iterator from which next element will come.
         */
        private Iterator<Pair<K, V>> bucketIterator;

        /**
         * Default constructor.
         */
        public Map4Iterator() {
            this.numberSeen = 0;
            this.currentBucket = 0;
            this.bucketIterator = Map4.this.hashTable.entry(0).iterator();
        }

        @Override
        public boolean hasNext() {
            return this.numberSeen < Map4.this.size;
        }

        @Override
        public Pair<K, V> next() {
            assert this.hasNext() : "Violation of: ~this.unseen /= <>";
            if (!this.hasNext()) {
                /*
                 * Exception is supposed to be thrown in this case, but with
                 * assertion-checking enabled it cannot happen because of assert
                 * above.
                 */
                throw new NoSuchElementException();
            }
            this.numberSeen++;
            if (this.bucketIterator.hasNext()) {
                return this.bucketIterator.next();
            }
            while (!this.bucketIterator.hasNext()) {
                this.currentBucket++;
                this.bucketIterator = Map4.this.hashTable.entry(
                        this.currentBucket).iterator();
            }
            return this.bucketIterator.next();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException(
                    "remove operation not supported");
        }

    }

}
