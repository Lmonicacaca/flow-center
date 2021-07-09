package com.flow.center.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class CommentDTO implements Serializable {
    private static final long serialVersionUID = -2222421908388039476L;
    protected String type;
    protected String userId;
    protected Date time;
    protected String taskId;
    protected String processInstanceId;
    protected String action;
    protected String message;
    protected String fullMessage;

    protected String id;
    protected boolean isInserted;
    protected boolean isUpdated;
    protected boolean isDeleted;
}
