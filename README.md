# Discord BOT Token Creator
⚠️ `(NOT A USER TOKEN CREATOR)` ⚠️

🔹 **A tool for creating Discord bot tokens, `great for premium or VIP bot systems!`**
This tool allows you to create as many Discord bot tokens as you need. It's safe to use and the code can be reviewed for added security. To avoid Discord limitations, it's recommended to use an alternate Discord account.

https://www.virustotal.com/gui/file/503aaa6e76e3966486b22eb0e07ce344a0256e0f780549ce064b3ab89f8c57d6

*Just because someone gave you a program and it was checked by VirusTotal does not necessarily mean that the program is safe to use.*

#

🔹 **Requirements**:
• Java 11+
- https://www.oracle.com/il-en/java/technologies/javase/jdk11-archive-downloads.html
- https://docs.aws.amazon.com/corretto/latest/corretto-11-ug/downloads-list.html
• Your Discord account's token (use an alternate account if you're concerned about security; you can also review the code or run it in a sandbox).

**⚠️ DO NOT DOWNLOAD ANY PROGRAM TO GET YOUR ACCOUNT'S TOKEN ⚠️**

• Your 2FA Discord secret (used to generate codes for creating bots; if using Authy, you'll need to generate a new secret)

• A working Discord bot token (used to send information to a Discord channel)

• The ID of a valid Discord text channel (the bot must be a member of the channel)

#

🔹**How To Use**:
- Download the latest release from https://github.com/WeeeeeeeeeeS/DiscordBotTokenCreator/releases.
- Open the downloaded file (if it doesn't work, open the terminal and run "java -jar release.jar").
- A Config.json file will be created. Fill in the required information in the file:
```
  "selfToken": your Discord token
  "secret": your 2FA secret key (Example: kjso efox en5x exsa) ( https://github.com/WeeeeeeeeeeS/DiscordBotTokenCreator/wiki )
  "channelId": the ID of the channel where the bot will send the tokens
  "standardName": the desired name for the bots
  "botTokenToSendInfo": a working Discord bot token
  ```
- Run the program and it will prompt you to enter the number of bots you want to create.

#

🔹 **How It Works**:
- A HTTP request is sent to create a team and the team's ID is stored in memory. The team's ID is then used to create a bot in that team. Every 25 bots, the tool creates a new team (since Discord limits each team to 25 bots)."

#
