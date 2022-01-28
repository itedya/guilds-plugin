package com.itedya.itedyaguilds.middlewares;

public abstract class AbstractHandler {
    protected AbstractHandler nextHandler;

    public AbstractHandler setNext(AbstractHandler handler) {
        this.nextHandler = handler;

        return handler;
    }

    public String handle() {
        if (nextHandler != null) {
            return nextHandler.handle();
        }

        return null;
    }
}
