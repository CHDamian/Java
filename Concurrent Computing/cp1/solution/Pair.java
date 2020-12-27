/** @file
 * Simple pair structure
 *
 * @author Damian Chańko <dc394127@students.mimuw.edu.pl>
 * @copyright Uniwersytet Warszawski
 * @date 17.12.2020
 */

package cp1.solution;

public class Pair <K,V>{

    // Atributes
    private K key;
    private V value;

    // Constructor
    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    // Getters
    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    // Setter (need only one for solution)
    public void setKey(K key){this.key = key;}
}
