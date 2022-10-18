package dto;

import lombok.*;

/**
 * @Author: liZiYuan
 * <p>
 * Date: 2022/10/15  21:24
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RpcMessage {
    /**
     * 消息类型
     */
    private byte messageType;
    /**
     * 序列化类型
     */
    private byte codec;
    /**
     * 压缩类型
     */
    private byte compress;
    /**
     * request id
     */
    private int requestId;
    /**
     * 内容
     */
    private Object data;
}
