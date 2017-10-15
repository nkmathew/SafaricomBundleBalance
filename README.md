### Safaricom Bundle Balance

Android app that displays your [Safaricom][5] bundle balance in periodic notifications
saving you the `*544#` dance

It simply pulls markup from Safaricom's [subscriber details page][1], parses it and
dumps the data in an sqlite database which is later retrieved and used for simple
artithmetic in determining how much data you've used in a certain period.

The frequency can be controlled from the settings page accessed through the actions
button on the top right.

#### Screenshots

![](https://i.imgur.com/1qVt1hJ.png)

![](https://i.imgur.com/3fJaBV6.png)

![](https://i.imgur.com/W3WoXyU.png)

![](https://i.imgur.com/cEo1tB7.png)

![](https://i.imgur.com/u0nc7ux.png)

#### Credits

+ [Apache Commons Lang][2]

    - The app uses string manipulation functions from this library licensed under
      Apache License 2.0

+ [OkHttp][3]

    - An HTTP & HTTP/2 client for Android and Java applications
    - Licensed also under Apache License 2.0

+ [jsoup][4]

    - A Java library for working with real-world HTML

+ SO Answers

    - Would be lost without these beautiful bastards

+ Icon obtained from **Deviant Art** made by **Gustavo** from Brazil

    - https://gustavogbr10.deviantart.com/art/Safaricom-Icon-281482144

---

[1]: http://www.safaricom.com/bundles/GetSubDetails
[2]: http://commons.apache.org/proper/commons-lang/
[3]: http://square.github.io/okhttp/
[4]: https://jsoup.org/
[5]: https://www.safaricom.co.ke/
