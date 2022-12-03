package leo.skvorc.racinggame.model;

import leo.skvorc.racinggame.Config;

import java.io.Serializable;

public class PlayerMetaData implements Serializable {

    private static final long serialVersionUID = 850L;

    private String ipAddress;
    private String port;
    private String playerName;
    private Config config;
    private Integer posX;
    private Integer posY;
    private Integer rotation;
    private Long pid;

    public PlayerMetaData() {
    }

    public PlayerMetaData(String ipAddress, String port, String playerName, Long pid) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.playerName = playerName;
        this.pid = pid;
    }

    public PlayerMetaData(String ipAddress, String port, Config config, Long pid) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.config = config;
        this.pid = pid;
    }

    public PlayerMetaData(String ipAddress, String port, String playerName, Integer posX, Integer posY, Integer rotation, Long pid) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.playerName = playerName;
        this.posX = posX;
        this.posY = posY;
        this.rotation = rotation;
        this.pid = pid;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
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

    public Integer getPosX() {
        return posX;
    }

    public void setPosX(Integer posX) {
        this.posX = posX;
    }

    public Integer getPosY() {
        return posY;
    }

    public void setPosY(Integer posY) {
        this.posY = posY;
    }

    public Integer getRotation() {
        return rotation;
    }

    public void setRotation(Integer rotation) {
        this.rotation = rotation;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }
}
