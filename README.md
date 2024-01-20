# Get Your Bombs off My Lawn!
A two-player game for Android. The goal is the destruction of your opponent's base by bombarding it with various missiles, and the protection of your own base by shielding it with constructions made of destructible blocks.
![Игровой процесс](Documentation/gamedesign_image3.png)
**Demo** [link](https://github.com/zhenyatos/GYBoML/issues/177#issuecomment-1767665735)

![Demo](Documentation/GYBoML.gif)

**Key features:**
1. Turn-based strategy with real-time physics
2. Easy to learn, hard to master

# Installation and launch instructions (for the user)
To install the latest version, download the application in the apk format from the attached files to the latest release. During installation, you will need to grant permission to install applications from third-party (non-Play Store) sources. You can confidently do this: our game is open-source and does not contain malicious components

# System requirements
1. Target platform - Android
2. Android version - 7.0 and above
3. Minimum RAM - 2 GB
4. Minimum screen resolution - 480 x 800

# Comparison with similar games
We were inspired by Angry Birds и Worms, let's look at how our project stands out
| Product                                        | GYBoML | Angry Birds | Worms         |
|-----------------------------------------------------------|--------|-------------|---------------|
| single-player             |    -   |      +      | +- (bots) |
| multi-player       |    +   |      -      |       +       |
| in-game economy         |    +   |      -      |       -       |
| cool physics                              |    +   |      +      |       -       |
| variativity in gameplay |    +   |      -      |       +       |


# Installation (for developers)
Latest release:
``` bash
	git clone https://github.com/zhenyatos/GYBoML/
```

## Building client application
* [Download](https://developer.android.com/studio) Android Studio
* In Android Studio:
	- Files...
	- Open...
	- Choose *client* directory
	- Build (Ctrl + F9)
	- For debugging Shift + F9, without debugging Shift + F10

## Building server application
### Просмотр и отладка кода
* [Download](https://www.jetbrains.com/ru-ru/idea/download/) Intellij Idea
* In Intellij Idea:
	- Files...
	- Open...
	- Choose *server* directory
	- Next, proceed as for building client application

### Building JAR executables for client and server

* Install the [Maven](https://maven.apache.org/download.cgi) utility for your operating system.
* Add the *apache_<version>/bin* directory address to the *PATH* environment variable.
* Ensure that the *JAVA_HOME* environment variable exists and points to a JDK no older than version 8.
* Navigate to the root of the project.
* Execute the command `mvn package`.
* Launch the network client:
  - Navigate to the directory net-client/target.
  - Execute the command:
    ```bash
    java -jar ???_WithDependencies.jar
    ```
    (The archive name is approximate and may change from version to version.)
* Launch the server:
  - Navigate to the directory server/target.
  - Similarly to the network client, execute the appropriate command.

To connect to the server, the client must send a request `req`. The server will then display information about the client connection in stdout.

To pass the turn to the client, write the command:
```bash
pass
```

To disconnect the client and server, write the following in the console:
```bash
q
```

The current version of the code is located in the develop branch. However, be cautious: some features may be unstable.

# Project team
* [Evgenii Samutichev](https://github.com/zhenyatos) - team leader
* [Konstantin Damaskinsky](https://github.com/kystyn) - tech lead
* [Arsen Varisov](https://github.com/Jiija) 
* [Grigory Petrunin](https://github.com/via8) 
* [Makar Solomatin](https://github.com/MakarSolomatin)

# Git workflow (in Russian)
[Статья atlassian](https://www.atlassian.com/git/tutorials/comparing-workflows/gitflow-workflow) и [статья на хабре](https://habr.com/ru/post/106912/)

В репозитории есть две основные ветки - **origin/master** и **origin/develop**. Сливать с ветвями напрямую нельзя. Необходимо сделать pull request, который будет разрешать техлид. Также присутствуют ветви, соответствующие задаче, в них реализуемой.

Ветка **origin/develop** является главной веткой разработки. Она всегда содержит собирающийся без ошибок код.
По окончании очередного спринта ветвь **origin/develop** при достаточной стабильности сливается в **origin/master**, образуя новый "*релиз*".

Каждая новая функциональность является соответствующей задачей (или *issue*) на ZenHub (или любой другой agile платформе) и имеет свое *название*, вкратце описывающее суть задачи. Для реализации соотвествующей функциональности надо создать новую ветку на базе ветки **origin/develop** в формате: **{номер задачи}-{название задачи}**. Это нужно для того, чтобы по ветке можно было легко найти задачу, которую реализует эта ветка, на agile платформе.

При успешной реализации функциональности соответствующая ей ветвь сливается обратно в develop, **предварительно делая git rebase**, если это необходимо (а именно - когда за время разработки этой функциональности произошли коммиты в **origin/develop** и нужно перебазировать текущую ветку для успешного слияния без конфликтов).

## Сообщения к коммитам:
Читайте https://m.habr.com/ru/post/416887/

1) Отделять заголовок от тела пустой строкой.
2) Ограничивать заголовок 50 символами.
3) Писать заголовок с заглавной буквы.
4) Не ставить точку в конце заголовка.
5) Использовать повелительное наклонение в заголовке.
6) Переходить на новую строку в теле на 72 символах.
7) В теле отвечать на вопросы: "Что?" и "Почему?", а не "Как?".
8) Писать #{номер issue} в заголовке

## Замечания:
1) Один коммит должен содержать одно целостное осмысленное добавление новой функциональности. Не надо делать несколько коммитов в 
ветке, реализуя некоторую функциональность (по типу 'Updated a.cpp' -> 'Updated b.cpp' -> 'Updated README.txt'). Обычно, одна ветка функциональности - это один коммит. Для "сплющивания" нескольких **последовательных**(!) коммитов в один используется 
git rebase -i COMMIT_SHA, где COMMIT_SHA - хэш коммита, идущего **перед первым** в серии сплющиваемых коммитов.
Если не понятно - подробнее [тут](https://htmlacademy.ru/blog/useful/git/how-to-squash-commits-and-why-it-is-needed)

## Стандарт кодирования
- Классы именуем по принципу: **каждое слово** с заглавной буквы:
	* class MainActivity
	* class BaseWeapon
- Последнее слово в имени класса обозначает категорию объектов, к которым он относится:
	* class MainActivity
	* class FirstLevel
	* class WoodDefense
- Функции и переменные именуем в camel-case:
	* Object getYourBombsOffMyLawn()
	* void setWeaponToViking(Gamer viking)
	* int bombsCount
- Аргументы функций при написании реализаций и при вызове не отделяем пробелами:
	* int getGamersCount(Forest f)
	* int gamersCount = getGamersCount(forest)
- Все вложенные по смыслу инструкции смещаем относительно родительских на четыре пробела (во всех IDEA-подобных IDE по умолчанию осуществляется нажатием Tab):
```java
for (int i = 0; i < 10; i++) {
	a[i] += i;
	if (a[i] == 0) {
	    a[i] = -1;
	    break;
	}
}
```
- Фигурные скобки для формирования блоков команд ставятся *всюду одинаково*: левая скобка на одной строке с инициатором блока (шапкой функции, условным оператором, циклом), правая скобка - на следующей строке после последней команды блока:
```java
Status foo(Object bar) {
	if (bar == null) {
		b = new Object(...);
		b.value = 0;
	}
}
```
- Все флаги оформляем в enum заглавными буквами:
```java
enum Status {
	OK(0),
	BAD(1);
	...
}
```
- *Общие правила*: все переменные должны именоваться так, чтобы по названию угадывалась их семантика. Не должно быть объектов с абстрактными многозначными *в контексте программы* именами.
	*Пример как не надо делать*:
```java
void printTwoWeirdNumbers(int[] array) {
	int cnt1 = 0, cnt2  = 0;
	for (int i = 0; i < array.length; i++)
		if (array[i] % 2 == 0)
			cnt1++;
		else
			cnt2++;
	System.out.println(...); // печатаем количества
}
```
*Вот так оптимально*:	
```java
void printEvenOddCount(int[] array) {
	int evenCount = 0, oddCount = 0;
	for (int i = 0; i < array.length; i++)
		if (array[i] % 2 == 0)
			evenCount++;
		else
			oddCount++;
	System.out.println(...); // печатаем количества
}
```
