package me.xploited.projecty.util;

import java.io.IOException;

@FunctionalInterface
public interface SupplierIO<T> {

    T get() throws IOException;

}
