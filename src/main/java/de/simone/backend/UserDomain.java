/*
 *   Copyright (c) 2025 Sauer Sitrone
 *   All rights reserved.
 *   I am Terry, and Terry has done this.
 */
package de.simone.backend;

import java.io.*;

/**
 * this is a marker interface to allow TAService to dimanicaly assign the
 * secondaryKey argument for a db operation. if a entity impements this
 * interface, mean that the value for secondaryKey field is the current user.
 * else, is the current Adult
 */
public interface UserDomain extends Serializable {

}
