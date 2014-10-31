package org.hamcrest.collection;

import static org.hamcrest.core.IsAnything.anything;
import static org.hamcrest.core.IsEqual.equalTo;

import java.util.Map.Entry;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import com.google.common.collect.Multimap;

/**
 * A hamcrest matcher for Multimaps.
 * 
 * @author alex
 * 
 * @param <K>
 * @param <V>
 */
public class IsMultimapContaining<K, V> extends TypeSafeMatcher<Multimap<? extends K, ? extends V>> {
  private final Matcher<? super K> keyMatcher;
  private final Matcher<? super V> valueMatcher;

  public IsMultimapContaining(final Matcher<? super K> keyMatcher, final Matcher<? super V> valueMatcher) {
    this.keyMatcher = keyMatcher;
    this.valueMatcher = valueMatcher;
  }

  @Override
  public boolean matchesSafely(final Multimap<? extends K, ? extends V> map) {
    for (final Entry<? extends K, ? extends V> entry : map.entries()) {
      if (keyMatcher.matches(entry.getKey()) && valueMatcher.matches(entry.getValue())) {
        return true;
      }
    }
    return false;
  }

  @Override
  public
      void
      describeMismatchSafely(final Multimap<? extends K, ? extends V> map, final Description mismatchDescription) {
    mismatchDescription.appendText("map was ").appendValueList("[", ", ", "]", map.entries());
  }

  @Override
  public void describeTo(final Description description) {
    description
        .appendText("map containing [")
        .appendDescriptionOf(keyMatcher)
        .appendText("->")
        .appendDescriptionOf(valueMatcher)
        .appendText("]");
  }

  /**
   * Creates a matcher for {@link Multimap}s matching when the examined
   * {@link Multimap} contains at least one entry whose key satisfies the
   * specified <code>keyMatcher</code> <b>and</b> whose value satisfies the
   * specified <code>valueMatcher</code>.
   * <p/>
   * For example:
   * 
   * <pre>
   * assertThat(myMap, hasMultiEntry(equalTo(&quot;bar&quot;), equalTo(&quot;foo&quot;)))
   * </pre>
   * 
   * @param keyMatcher
   *          the key matcher that, in combination with the valueMatcher, must
   *          be satisfied by at least one entry
   * @param valueMatcher
   *          the value matcher that, in combination with the keyMatcher, must
   *          be satisfied by at least one entry
   */
  @Factory
  public static <K, V> Matcher<Multimap<? extends K, ? extends V>> hasMultiEntry(
      final Matcher<? super K> keyMatcher,
      final Matcher<? super V> valueMatcher) {
    return new IsMultimapContaining<K, V>(keyMatcher, valueMatcher);
  }

  /**
   * Creates a matcher for {@link Multimap}s matching when the examined
   * {@link Multimap} contains at least one entry whose key equals the specified
   * <code>key</code> <b>and</b> whose value equals the specified
   * <code>value</code>.
   * <p/>
   * For example:
   * 
   * <pre>
   * assertThat(myMap, hasMultiEntry(&quot;bar&quot;, &quot;foo&quot;))
   * </pre>
   * 
   * @param key
   *          the key that, in combination with the value, must be describe at
   *          least one entry
   * @param value
   *          the value that, in combination with the key, must be describe at
   *          least one entry
   */
  @Factory
  public static <K, V> Matcher<Multimap<? extends K, ? extends V>> hasMultiEntry(final K key, final V value) {
    return new IsMultimapContaining<K, V>(equalTo(key), equalTo(value));
  }

  /**
   * Creates a matcher for {@link Multimap}s matching when the examined
   * {@link Multimap} contains at least one key that satisfies the specified
   * matcher.
   * <p/>
   * For example:
   * 
   * <pre>
   * assertThat(myMap, hasKey(equalTo(&quot;bar&quot;)))
   * </pre>
   * 
   * @param keyMatcher
   *          the matcher that must be satisfied by at least one key
   */
  @Factory
  public static <K> Matcher<Multimap<? extends K, ?>> hasKey(final Matcher<? super K> keyMatcher) {
    return new IsMultimapContaining<K, Object>(keyMatcher, anything());
  }

  /**
   * Creates a matcher for {@link Multimap}s matching when the examined
   * {@link Multimap} contains at least one key that is equal to the specified
   * key.
   * <p/>
   * For example:
   * 
   * <pre>
   * assertThat(myMap, hasKey(&quot;bar&quot;))
   * </pre>
   * 
   * @param key
   *          the key that satisfying maps must contain
   */
  @Factory
  public static <K> Matcher<Multimap<? extends K, ?>> hasKey(final K key) {
    return new IsMultimapContaining<K, Object>(equalTo(key), anything());
  }

  /**
   * Creates a matcher for {@link Multimap}s matching when the examined
   * {@link Multimap} contains at least one value that satisfies the specified
   * valueMatcher.
   * <p/>
   * For example:
   * 
   * <pre>
   * assertThat(myMap, hasValue(equalTo(&quot;foo&quot;)))
   * </pre>
   * 
   * @param valueMatcher
   *          the matcher that must be satisfied by at least one value
   */
  @Factory
  public static <V> Matcher<Multimap<?, ? extends V>> hasValue(final Matcher<? super V> valueMatcher) {
    return new IsMultimapContaining<Object, V>(anything(), valueMatcher);
  }

  /**
   * Creates a matcher for {@link Multimap}s matching when the examined
   * {@link Multimap} contains at least one value that is equal to the specified
   * value.
   * <p/>
   * For example:
   * 
   * <pre>
   * assertThat(myMap, hasValue(&quot;foo&quot;))
   * </pre>
   * 
   * @param value
   *          the value that satisfying maps must contain
   */
  @Factory
  public static <V> Matcher<Multimap<?, ? extends V>> hasValue(final V value) {
    return new IsMultimapContaining<Object, V>(anything(), equalTo(value));
  }
}
