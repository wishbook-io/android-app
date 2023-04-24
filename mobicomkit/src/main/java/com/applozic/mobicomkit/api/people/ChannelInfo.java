package com.applozic.mobicomkit.api.people;


import com.applozic.mobicommons.json.JsonMarker;
import com.applozic.mobicommons.people.channel.Channel;
import com.applozic.mobicommons.people.channel.ChannelMetadata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sunil on 29/1/16.
 */
public class ChannelInfo extends JsonMarker {

    private String clientGroupId;
    private String groupName;
    List<GroupUser> users;
    private List<String> groupMemberList;
    private String imageUrl;
    private int type = Channel.GroupType.PUBLIC.getValue().intValue();
    private Map<String, String> metadata;
    private String admin;
    private Integer parentKey;
    private String parentClientGroupId;
    private ChannelMetadata channelMetadata;

    public ChannelInfo(String groupName, List<String> groupMemberList) {
        this.groupName = groupName;
        this.groupMemberList = groupMemberList;
    }

    public ChannelInfo(String groupName, List<String> groupMemberList, String imageLink) {
        this.groupName = groupName;
        this.groupMemberList = groupMemberList;
        this.imageUrl = imageLink;
    }

    public String getClientGroupId() {
        return clientGroupId;
    }

    public void setClientGroupId(String clientGroupId) {
        this.clientGroupId = clientGroupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<String> getGroupMemberList() {
        return groupMemberList;
    }

    public void setGroupMemberList(List<String> groupMemberList) {
        this.groupMemberList = groupMemberList;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public ChannelMetadata getChannelMetadata() {
        return channelMetadata;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public List<GroupUser> getUsers() {
        return users;
    }

    public void setUsers(List<GroupUser> users) {
        this.users = users;
    }

    public Integer getParentKey() {
        return parentKey;
    }

    public void setParentKey(Integer parentKey) {
        this.parentKey = parentKey;
    }

    public String getParentClientGroupId() {
        return parentClientGroupId;
    }

    public void setParentClientGroupId(String parentClientGroupId) {
        this.parentClientGroupId = parentClientGroupId;
    }

    public void setChannelMetadata(ChannelMetadata channelMetadata) {
        this.channelMetadata = channelMetadata;
        if (channelMetadata != null) {
            if (metadata == null) {
                metadata = new HashMap<String, String>();
            }
            metadata.put(ChannelMetadata.CREATE_GROUP_MESSAGE, channelMetadata.getCreateGroupMessage());
            metadata.put(ChannelMetadata.ADD_MEMBER_MESSAGE, channelMetadata.getAddMemberMessage());
            metadata.put(ChannelMetadata.GROUP_NAME_CHANGE_MESSAGE, channelMetadata.getGroupNameChangeMessage());
            metadata.put(ChannelMetadata.GROUP_ICON_CHANGE_MESSAGE, channelMetadata.getGroupIconChangeMessage());
            metadata.put(ChannelMetadata.GROUP_LEFT_MESSAGE, channelMetadata.getGroupLeftMessage());
            metadata.put(ChannelMetadata.JOIN_MEMBER_MESSAGE, channelMetadata.getJoinMemberMessage());
            metadata.put(ChannelMetadata.DELETED_GROUP_MESSAGE, channelMetadata.getDeletedGroupMessage());
            metadata.put(ChannelMetadata.REMOVE_MEMBER_MESSAGE, channelMetadata.getRemoveMemberMessage());
            metadata.put(ChannelMetadata.HIDE_METADATA_NOTIFICATION, channelMetadata.getHideMetaDataNotification() + "");
            metadata.put(ChannelMetadata.MUTE, channelMetadata.isDefaultMute() + "");
        }
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public class GroupUser {
        String userId;
        int groupRole;

        public String getUserId() {
            return userId;
        }

        public GroupUser setUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public int getGroupRole() {
            return groupRole;
        }

        public GroupUser setGroupRole(int groupRole) {
            this.groupRole = groupRole;
            return this;
        }

        @Override
        public String toString() {
            return "GroupUser{" +
                    "userId='" + userId + '\'' +
                    ", groupRole=" + groupRole +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "ChannelInfo{" +
                "clientGroupId='" + clientGroupId + '\'' +
                ", groupName='" + groupName + '\'' +
                ", users=" + users +
                ", groupMemberList=" + groupMemberList +
                ", imageUrl='" + imageUrl + '\'' +
                ", type=" + type +
                ", metadata=" + metadata +
                ", admin='" + admin + '\'' +
                ", parentKey=" + parentKey +
                ", channelMetadata=" + channelMetadata +
                '}';
    }
}
