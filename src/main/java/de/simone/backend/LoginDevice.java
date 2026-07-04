/*
 *   Copyright (c) 2026 Sauer Sitrone
 *   All rights reserved.
 *   I am Terry, and Terry has done this.
 */
package de.simone.backend;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import de.simone.UIUtils;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.transaction.Transactional;

@Entity
@Table(name = "LoginDevices")
public class LoginDevice extends TAEntity implements UserDomain {

    public String ipAddress = ""; // avoid error on deploy O.o
    public String cityName;
    public String regionName;
    public String os;
    public String browser;
    public String blockReason;
    public LocalDateTime lastSignIn;
    public LocalDateTime blockAt;
    public boolean isBlocked;

    public String getLocation() {
        return cityName + ", " + regionName;
    }

    public String getOsIcon() {
        return UIUtils.ICON_PATH + os + "-device.png";
    }

    /**
     * Return an stored reference of the current device where the user has logged in
     * or empty option if is there no registred instance of the current device.
     * 
     * @param ipAddress - the ip address
     * @param location  - the location
     * @param os        - the os
     * @return the device
     */
    public static Optional<LoginDevice> getStoredDevice(Long userId, String ipAddress, String location, String os) {
        List<LoginDevice> devices = LoginDevice.list("secondaryKey = ?1", userId);
        Optional<LoginDevice> optional = devices.stream().filter(d -> d.ipAddress.equals(ipAddress) &&
                d.getLocation().equals(location) && d.os.equals(os))
                .findFirst();
        return optional;
    }

    /**
     * Store the current device if is not stored yet. Return true if the device is
     * new, false if the device is already stored.
     * 
     * @param userId - the user id
     * @param device - the device
     * @return true if the device is new, false if the device is already stored
     */
    @Transactional
    public static boolean storeDevice(Long userId, LoginDevice device) {
        Optional<LoginDevice> optional = getStoredDevice(userId, device.ipAddress, device.getLocation(), device.os);
        boolean isNew = !optional.isPresent();
        if (isNew) {
            device.setSecondaryKey(userId);
            device.lastSignIn = LocalDateTime.now();
            device.persist();
        } else {
            LoginDevice device2 = optional.get();
            device2.lastSignIn = LocalDateTime.now();
        }
        return isNew;
    }
}
