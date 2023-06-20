# availability-checker
A Selenium command line runner to check availability of certain things on certain websites, such as tickets to events, appointments etc.

- `WebsiteChecker` interface can be implemented to check various websites for some condition. [Selenium](https://www.selenium.dev/) is the option for scraping.
- `Notifier` interface can implemented to send notifications to wherever, currently Telegram is supported via [ktgbotapi](https://github.com/InsanusMokrassar/ktgbotapi).

Runs with Cron on my Raspberry Pi🤷‍♂️
