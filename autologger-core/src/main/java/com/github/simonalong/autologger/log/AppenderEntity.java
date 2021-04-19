package com.github.simonalong.autologger.log;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author shizi
 * @since 2021-04-13 13:48:58
 */
@Data
@AllArgsConstructor
public class AppenderEntity {

    private String appenderName;
    private String appenderPattern;
}
