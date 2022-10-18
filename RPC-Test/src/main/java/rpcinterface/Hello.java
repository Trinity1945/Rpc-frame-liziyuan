package rpcinterface;

import lombok.*;

import java.io.Serializable;

/**
 * @Author: liZiYuan
 * <p>
 * Date: 2022/10/18  15:18
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Hello implements Serializable {
    private String message;
    private String description;
}
