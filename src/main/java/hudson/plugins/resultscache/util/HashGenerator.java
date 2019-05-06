// Copyright (C) king.com Ltd 2019
// https://github.com/jenkinsci/results-cache-plugin
// License: Apache 2.0, https://raw.githubusercontent.com/jenkinsci/results-cache-plugin/master/LICENSE-APACHE

package hudson.plugins.resultscache.util;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * Hash Generator utility
 */
public class HashGenerator {

    /**
     * Returns a MD5 hash from a string
     * @param data data to be hashed
     * @return MD5 hash from a string
     */
    public String getHash(String data) {
        return DigestUtils.md5Hex(data);
    }
}
