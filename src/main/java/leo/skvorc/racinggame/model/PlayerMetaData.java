package leo.skvorc.racinggame.model;

import leo.skvorc.racinggame.Config;

import java.io.Serializable;

public class PlayerMetaData implements Serializable {

    private static final long serialVersionUID = 850L;

    private String ipAddress;
    private Integer port;
    private String playerName;
    private Config config;
    private Long pid;

    public PlayerMetaData() {
    }

    public PlayerMetaData(String ipAddress, Integer port, String playerName, Long pid) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.playerName = playerName;
        this.pid = pid;
    }

    public PlayerMetaData(String ipAddress, Integer port, Config config, Long pid) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.config = config;
        this.pid = pid;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }
}
