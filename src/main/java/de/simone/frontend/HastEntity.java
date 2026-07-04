/*
 *   Copyright (c) 2025 Sauer Sitrone
 *   All rights reserved.
 *   I am Terry, and Terry has done this.
 */
package de.simone.frontend;

import de.simone.backend.TAEntity;

public interface HastEntity<E extends TAEntity> {

    abstract E getEntity();

    abstract void setEntity(E entity);

    abstract boolean isValid();

}
