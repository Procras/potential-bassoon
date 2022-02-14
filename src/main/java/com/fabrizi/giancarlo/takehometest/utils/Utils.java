package com.fabrizi.giancarlo.takehometest.utils;

import com.fabrizi.giancarlo.takehometest.controller.Controller;
import com.fabrizi.giancarlo.takehometest.pojo.Commit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.function.Predicate;

/**
 * The Class Utils.
 */
public class Utils {

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(Controller.class);


    public static <T> T findByProperty(Collection<T> col, Predicate<T> filter) {
        return col.stream().filter(filter).findFirst().orElse(null);
    }
}
