package com.kshired.lox;

import java.util.List;

abstract class Expr {
    interface Visitor<R> {
        R visitBinaryExpr(Binary expr);

        R visitGroupingExpr(Grouping expr);

        R visitLiteralExpr(Literal expr);

        R visitUnaryExpr(Unary expr);
    }

    static class Binary extends Expr {
        private final Expr left;
        private final Token operator;
        private final Expr right;

        Binary(Expr left, Token operator, Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitBinaryExpr(this);
        }

        public Expr getLeft() {
            return left;
        }

        public Token getOperator() {
            return operator;
        }

        public Expr getRight() {
            return right;
        }
    }

    static class Grouping extends Expr {
        private final Expr expression;

        Grouping(Expr expression) {
            this.expression = expression;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitGroupingExpr(this);
        }

        public Expr getExpression() {
            return expression;
        }
    }

    static class Literal extends Expr {
        private final Object value;

        Literal(Object value) {
            this.value = value;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitLiteralExpr(this);
        }

        public Object getValue() {
            return value;
        }
    }

    static class Unary extends Expr {
        private final Token operator;
        private final Expr right;

        Unary(Token operator, Expr right) {
            this.operator = operator;
            this.right = right;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitUnaryExpr(this);
        }

        public Token getOperator() {
            return operator;
        }

        public Expr getRight() {
            return right;
        }
    }

    abstract <R> R accept(Visitor<R> visitor);
}
