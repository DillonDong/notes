package com.example.demo;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author:fudingcheng
 * @date:2019-02-14
 * @description:
 */
@Component
@ConfigurationProperties(prefix = "my")
public class Config {

    private List<String> servers = new ArrayList<String>();

    private Map<String,String> map = new HashMap<String,String>();

    private List<Map<String,String>> lMap;

    public void setlMap(List<Map<String, String>> lMap) {
        this.lMap = lMap;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }

    public Map<String, String> getMap() {
        return map;
    }

    public List<String> getServers() {
        return this.servers;
    }

    public void setServers(List<String> servers) {
        this.servers = servers;
    }

    @Override
    public String toString() {
        return "Config{" +
                "servers=" + servers +
                ", map=" + map +
                ", lMap=" + lMap +
                '}';
    }
}
