/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.fangtang.tv.sdk.base.core;


/**
 * Priority levels recognized by the image pipeline.
 */
public enum Priority {
    /**
     * NOTE: DO NOT CHANGE ORDERING OF THOSE CONSTANTS UNDER ANY CIRCUMSTANCES.
     * Doing so will make ordering incorrect.
     */

    /**
     * Lowest priority level. Used for prefetches of non-visible images.
     */
    LOW,

    /**
     * Medium priority level. Used for warming of images that might soon get visible.
     */
    MEDIUM,

    /**
     * Highest priority level. Used for images that are currently visible on screen.
     */
    HIGH;

    /**
     * Gets the higher priority among the two.
     *
     * @param priority1
     * @param priority2
     * @return higher priority
     */
    public static Priority getHigherPriority(
            Priority priority1,
            Priority priority2) {
        if (priority1 == null) {
            return priority2;
        }
        if (priority2 == null) {
            return priority1;
        }
        if (priority1.ordinal() > priority2.ordinal()) {
            return priority1;
        } else {
            return priority2;
        }
    }

}
