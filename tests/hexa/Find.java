package hexa;

import java.util.List;

class Find {

    private static final List<String> PACKAGES = List.of(
            "hexa",
            "hexa.domain",
            "hexa.application",
            "hexa.application.port.in",
            "hexa.application.port.out",
            "hexa.adapter.in",
            "hexa.adapter.out");

    static Class<?> type(String simpleName) throws ClassNotFoundException {
        for (String pkg : PACKAGES) {
            try {
                return Class.forName(pkg + "." + simpleName);
            } catch (ClassNotFoundException e) {
            }
        }
        throw new ClassNotFoundException(simpleName);
    }

    static boolean exists(String simpleName) {
        try {
            type(simpleName);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    static Object newBookStore() throws Exception {
        String name = exists("MemoryBookAdapter") ? "MemoryBookAdapter" : "BookRepository";
        return type(name).getConstructor().newInstance();
    }

    static Object newLoanService() throws Exception {
        Class<?> service = type("LoanService");
        if (exists("BookPort")) {
            try {
                return service.getConstructor(type("BookPort")).newInstance(newBookStore());
            } catch (NoSuchMethodException e) {
            }
        }
        return service.getConstructor().newInstance();
    }
}
