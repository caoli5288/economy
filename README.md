# Economy
Economy plugin for bukkit based minecraft server.

## Feature
* Database supported.
* Non-blocking query.
* Vault hook support.

## Command
* /money give \<player> \<count>
    * Give target player some money.
    * Permission `money.give` required.
    * Permission `money.admin` required for free pay.
* /money take \<player> \<count>
    * Remove some money from target player's account.
    * Permission `money.take` required.
* /money look \<player>
    * Look target player's account.
    * Permission `money.look` required.
* /money set \<player> \<count>
    * Set target player's account to special count.
    * Permission `money.set` required.
* /money top
    * Query top 20 rich-man. Result cached 5 minutes.
    * Permission `money.top` required.