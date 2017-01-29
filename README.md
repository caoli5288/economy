# Economy
简单的MC服务器经济插件。

## Feature
* 默认的数据库支持
* 无阻塞查询
* 支持Vault关联

## Command
* /money give \<player> \<count>
    * Give target player some money.
    * Permission `money.give` required.
    * Permission `money.admin` required for free pay.
* /money take \<player> \<count>
    * Remove some money from target player's account.
    * Permission `money.admin` required.
* /money look \<player>
    * Look target player's account.
    * Permission `money.look` required.
* /money set \<player> \<count>
    * Set target player's account to special count.
    * Permission `money.set` required.
* /money top
    * Query top 20 rich-man. Result cached 5 minutes.
    * Permission `money.top` required.
    
## Misc
* 关于浮点约数
    * 采用四舍五入约数。如默认约数两位，则0.998会被约成1.0。
    * 余额判断时不会发生约数。如判断某玩家（余额0）是否拥有0.001结果为假。
    * 账户操作时先对传入约数，如约后为0则抛出异常。
    * 账户操作时以实数进行计算，结果进行约数操作后写入。
