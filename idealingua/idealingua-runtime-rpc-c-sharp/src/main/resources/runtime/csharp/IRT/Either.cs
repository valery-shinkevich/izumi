
using System;

namespace IRT {
    public abstract class Either<LT, RT> {
        private Either() {}
        public abstract Either<LT, B> Map<B>(Func<RT, B> f);
        public abstract Either<V, B> BiMap<V, B>(Func<RT, B> g, Func<LT, V> f);
        public abstract B Fold<B>(Func<RT, B> whenRight, Func<LT, B> whenLeft);
        public abstract LT GetOrElse(LT l);
        public abstract LT GetLeft();
        public abstract RT GetOrElse(RT a);
        public abstract RT GetRight();
        public abstract bool IsLeft();
        public abstract bool IsRight();
        public abstract Either<RT, LT> Swap();
        public abstract Either<LT, RT> FilterOrElse(Func<RT, bool> p, LT zero);
        public abstract void Match(Action<RT> whenRight, Action<LT> whenLeft);

        public static explicit operator LT(Either<LT, RT> e) {
            if (!e.IsLeft()) {
                throw new InvalidCastException("Either is not in the Left state.");
            }

            return e.GetLeft();
        }

        public static explicit operator RT(Either<LT, RT> e) {
            if (e.IsLeft()) {
                throw new InvalidCastException("Either is not in the Right state.");
            }

            return e.GetRight();
        }

        public static implicit operator Either<LT, RT> (LT value) {
            return new Left<LT, RT>(value);
        }

        public static implicit operator Either<LT, RT> (RT value) {
            return new Right<LT, RT>(value);
        }

        public sealed class Left<L, A>: Either<L, A> {
            private readonly L Value;
            public Left(L value) {
                Value = value;
            }

            public override Either<L, B> Map<B>(Func<A, B> f) {
                return new Left<L, B>(Value);
            }

            public override Either<V, B> BiMap<V, B>(Func<A, B> g, Func<L, V> f) {
                return new Left<V, B>(f(Value));
            }

            public override B Fold<B>(Func<A, B> whenRight, Func<L, B> whenLeft) {
                return whenLeft(Value);
            }

            public override L GetOrElse(L l) {
                return Value;
            }

            public override A GetOrElse(A a) {
                return a;
            }

            public override L GetLeft() {
                return Value;
            }

            public override A GetRight() {
                throw new InvalidCastException("Either is not in the Right state.");
            }

            public override bool IsLeft() {
                return true;
            }

            public override bool IsRight() {
                return false;
            }

            public override Either<A, L> Swap() {
                return new Right<A, L>(Value);
            }

            public override Either<L, A> FilterOrElse(Func<A, bool> p, L zero) {
                return this;
            }

            public override void Match(Action<A> whenRight, Action<L> whenLeft) {
                whenLeft(Value);
            }
        }

        public sealed class Right<L, A>: Either<L, A> {
            private readonly A Value;
            public Right(A value) {
                Value = value;
            }

            public override Either<L, B> Map<B>(Func<A, B> f) {
                return new Right<L, B>(f(Value));
            }

            public override Either<V, B> BiMap<V, B>(Func<A, B> g, Func<L, V> f) {
                return new Right<V, B>(g(Value));
            }

            public override B Fold<B>(Func<A, B> whenRight, Func<L, B> whenLeft) {
                return whenRight(Value);
            }

            public override L GetOrElse(L l) {
                return l;
            }

            public override A GetOrElse(A a) {
                return Value;
            }

            public override L GetLeft() {
                throw new InvalidCastException("Either is not in the Left state.");
            }

            public override A GetRight() {
                return Value;
            }

            public override bool IsLeft() {
                return false;
            }

            public override bool IsRight() {
                return true;
            }

            public override Either<A, L> Swap() {
                return new Left<A, L>(Value);
            }

            public override Either<L, A> FilterOrElse(Func<A, bool> p, L zero) {
                if (p(Value)) {
                    return this;
                }

                return new Left<L, A>(zero);
            }

            public override void Match(Action<A> whenRight, Action<L> whenLeft) {
                whenRight(Value);
            }
        }
    }
}