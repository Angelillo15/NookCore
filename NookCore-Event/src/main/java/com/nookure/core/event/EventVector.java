package com.nookure.core.event;

import java.lang.reflect.Method;

public record EventVector(Method method, Object listener, NookSubscribe nookSubscribe) {
}
