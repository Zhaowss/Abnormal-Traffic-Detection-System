package com.company.encryptedtrafficclassifier.common.utils;

public class SystemFilterBuildUtil {

    public static String buildFilter(String protocol, Long port, String sourceIp, String destinationIp) {
        StringBuilder filter = new StringBuilder();
        if (protocol != null && !protocol.isEmpty()) {
            filter.append(protocol);
        }
        if (port != null) {
            if (filter.length() > 0) {
                filter.append(" and ");
            }
            filter.append("port ").append(port);
        }
        if (sourceIp != null && !sourceIp.isEmpty()) {
            if (filter.length() > 0) {
                filter.append(" and ");
            }
            filter.append("src host ").append(sourceIp);
        }
        if (destinationIp != null && !destinationIp.isEmpty()) {
            if (filter.length() > 0) {
                filter.append(" and ");
            }
            filter.append("dst host ").append(destinationIp);
        }

        return filter.length() > 0 ? filter.toString() : "ip";
    }

}