package com.github.simonalong.autologger.log;

import lombok.*;

/**
 * @author shizi
 * @since 2021-02-05 11:45:48
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "logFunId")
public class LogFunEntity {

    private String logFunId;
    private String logFunName;
}
