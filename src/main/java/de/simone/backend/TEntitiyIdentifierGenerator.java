/*
 *   Copyright (c) 2025 Sauer Sitrone
 *   All rights reserved.
 *   I am Terry, and Terry has done this.
 */

package de.simone.backend;

import java.io.Serializable;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

public class TEntitiyIdentifierGenerator implements IdentifierGenerator {

    private static long epoch = 1692020217414L; // 2023-08-14 15:37:00
    private static long previousTimeMillis = System.currentTimeMillis() - epoch;
    private static long counter = 0L;

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        return nextIDTerry();
    }

    public static synchronized long nextIDTerry() {
        long currentTimeMillis = System.currentTimeMillis() - epoch;
        counter = (currentTimeMillis == previousTimeMillis) ? (counter + 1L) & 4095L : 0L;
        previousTimeMillis = currentTimeMillis;
        long timeComponent = (currentTimeMillis & 8796093022207L) << 12;
        return timeComponent | counter;
    }

    /**
     * https://stackoverflow.com/questions/15184820/how-to-generate-unique-positive-long-using-uuid
     * 
     * @return the next id
     */
    public static synchronized long nextID() {
        long currentTimeMillis = System.currentTimeMillis() - epoch;
        counter = (currentTimeMillis == previousTimeMillis) ? (counter + 1L) & 1048575L : 0L;
        previousTimeMillis = currentTimeMillis;
        long timeComponent = (currentTimeMillis & 8796093022207L) << 20;
        return timeComponent | counter;
    }
}