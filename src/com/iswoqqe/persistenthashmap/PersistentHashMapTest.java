package com.iswoqqe.persistenthashmap;

import org.junit.Test;

import java.util.Random;

import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class PersistentHashMapTest {
    @Test
    public void basicUsage() {
        PersistentHashMap<Integer, Object> map1 = new PersistentHashMap<>();
        PersistentHashMap<Integer, Object> map2 = map1.with(1, 'a');
        PersistentHashMap<Integer, Object> map3 = map2.with(2, 'b');

        assertNull(map1.get(1));
        assertEquals('a', map2.get(1));
        assertEquals('a', map3.get(1));

        assertNull(map1.get(2));
        assertNull(map2.get(2));
        assertEquals('b', map3.get(2));

        assertTrue(map3.hasKey(1));
        assertTrue(map3.hasKey(2));

        assertTrue(map2.hasKey(1));
        assertTrue(!map2.hasKey(2));

        assertTrue(!map1.hasKey(1));
        assertTrue(!map1.hasKey(2));
    }

    @Test
    public void testNullVal() {
        PersistentHashMap<Integer, Object> map = new PersistentHashMap<>();

        for (int iter = 0; iter < 2; ++iter) {
            for (int i = 0; i < 1000; ++i) {
                if ((i & 1) == 1) {
                    map = map.with(i, null);
                    assertNull(map.get(i, "notnull"));
                } else {
                    map = map.with(i, 'a');
                    assertEquals(map.get(i), 'a');
                }
                assertTrue(map.hasKey(i));
            }
        }
    }

    @Test
    public void gettingOldValues() {
        PersistentHashMap<Integer, Object> map0 = new PersistentHashMap<>();
        PersistentHashMap<Integer, Object> map1 = map0.with(1, 'a');
        PersistentHashMap<Integer, Object> map2 = map1.with(2, 'b');
        PersistentHashMap<Integer, Object> map3 = map2.with(3, 'c');

        assertNull(map0.get(0));
        assertNull(map1.get(0));
        assertNull(map2.get(0));
        assertNull(map3.get(0));

        assertNull(map0.get(1));
        assertEquals('a', map1.get(1));
        assertEquals('a', map2.get(1));
        assertEquals('a', map3.get(1));

        assertNull(map0.get(2));
        assertNull(map1.get(2));
        assertEquals('b', map2.get(2));
        assertEquals('b', map3.get(2));

        assertNull(map0.get(3));
        assertNull(map1.get(3));
        assertNull(map2.get(3));
        assertEquals('c', map3.get(3));
    }

    @Test
    public void reassignment() {
        PersistentHashMap<Integer, Object> map = new PersistentHashMap<>();
        map = map.with(1, 'a');
        assertEquals('a', map.get(1));
        map = map.with(1, 'b');
        assertEquals('b', map.get(1));
    }

    @Test
    public void randomValues() {
        PersistentHashMap<Object, Object> map = new PersistentHashMap<>();
        Random rand = new Random(123);

        for (int i = 0; i < 10000000; ++i) {
            Long k = rand.nextLong();
            Long v = rand.nextLong();

            map = map.with(k, v);

            assertTrue(map.hasKey(k));

            if (!v.equals(map.get(k))) {
                fail("Expected: map.get(" + k + ") == " + v + ", not " + map.get(k) + "    " + i + " iterations");
            }
        }
    }

    @Test
    public void collision() {
        PersistentHashMap<CollisionClass, Object> map = new PersistentHashMap<>();
        map = map.with(new CollisionClass(123), 'a');
        map = map.with(new CollisionClass(321), 'b');
        assertEquals('a', map.get(new CollisionClass(123)));
        assertEquals('b', map.get(new CollisionClass(321)));

        assertTrue(map.hasKey(new CollisionClass(123)));
        assertTrue(map.hasKey(new CollisionClass(321)));
    }

    @Test
    public void reassignmentWithCollision() {
        PersistentHashMap<CollisionClass, Object> map = new PersistentHashMap<>();
        map = map.with(new CollisionClass(1), 'a');
        assertEquals('a', map.get(new CollisionClass(1)));
        map = map.with(new CollisionClass(1), 'b');
        assertEquals('b', map.get(new CollisionClass(1)));
    }

    private class CollisionClass {
        final int i;

        CollisionClass(int i) {
            this.i = i;
        }

        @Override
        public boolean equals(Object o) {
            return (o instanceof CollisionClass) && (this.i == ((CollisionClass)o).i);
        }

        @Override
        public int hashCode() {
            return 123;
        }
    }
}