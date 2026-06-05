package com.hugmom.back.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 妈妈回复实体类 - 爱的回响 · 双向温暖闭环
 * 
 * @author HugMom Team
 */
@Data
public class MotherReply implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 回复唯一标识（业务主键）
     */
    private String replyId;

    /**
     * 接收回复的用户ID
     */
    private String userId;

    /**
     * 发送回复的成员ID（妈妈）
     */
    private String memberId;

    /**
     * 原始消息ID（拥抱或平安消息）
     */
    private String originalMessageId;

    /**
     * 原始消息类型（hug:拥抱 safety:平安消息）
     */
    private String originalMessageType;

    /**
     * 回复类型（voice:语音 text:文字 emoji:表情）
     */
    private String replyType;

    /**
     * 回复内容
     */
    private String content;

    /**
     * 语音文件URL（如果是语音回复）
     */
    private String voiceUrl;

    /**
     * AI转文字结果（语音消息时生成）
     */
    private String aiTranscript;

    /**
     * 情感标签（warm:温暖 caring:关心 proud:骄傲）
     */
    private String emotion;

    /**
     * 是否已读（1:已读 0:未读）
     */
    private Boolean isRead;

    /**
     * 接收时间
     */
    private LocalDateTime receiveTime;

    /**
     * 阅读时间
     */
    private LocalDateTime readTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
