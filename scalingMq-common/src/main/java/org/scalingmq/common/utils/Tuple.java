package org.scalingmq.common.utils;

/**
 * 元组封装
 * @author renyansong
 */
public class Tuple {

    private Tuple(){}

    public static <A,B> TwoTuple<A,B> tuple(A a,B b){
        return new TwoTuple<A, B>(a,b);
    }

    public static <A,B,C> ThreeTuple<A,B,C> tuple(A a,B b,C c){
        return new ThreeTuple<A, B, C>(a,b,c);
    }

    public static class TwoTuple<A, B> {

        public A first;

        public B second;

        public TwoTuple(A a, B b){
            first = a;
            second = b;
        }

        public void update(A a, B b) {
            first = a;
            second = b;
        }
    }

    public static class ThreeTuple<A, B, C> {

        public final A first;

        public final B second;

        public final C third;

        public ThreeTuple(A a, B b, C c){
            first = a;
            second = b;
            third = c;
        }
    }

}
