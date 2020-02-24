# Get Your Bombs off My Lawn!

# Участники проекта 
* [Самутичев Евгений](https://github.com/zhenyatos) - тимлид
* [Дамаскинский Константин](https://github.com/kystyn) - техлид
* [Варисов Арсен](https://github.com/Jiija) 
* [Петрунин Григорий](https://github.com/via8) 
* [Соломатин Макар](https://github.com/MakarSolomatin)


# Git workflow
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

## Замечания:
1) Один коммит должен содержать одно целостное осмысленное добавление новой функциональности. Не надо делать несколько коммитов в 
ветке, реализуя некоторую функциональность (по типу 'Updated a.cpp' -> 'Updated b.cpp' -> 'Updated README.txt'). Обычно, одна ветка функциональности - это один коммит. Для "сплющивания" нескольких **последовательных**(!) коммитов в один используется 
git rebase -i COMMIT_SHA, где COMMIT_SHA - хэш коммита, идущего **перед первым** в серии сплющиваемых коммитов.
Если не понятно - подробнее [тут](https://htmlacademy.ru/blog/useful/git/how-to-squash-commits-and-why-it-is-needed

