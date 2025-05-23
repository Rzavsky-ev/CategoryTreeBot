# Category Manager Bot

## Описание

Telegram-бот для управления иерархической структурой категорий с возможностью экспорта/импорта в Excel.

## Команды

| Команда                             | Описание                                          | Пример                                  |
|-------------------------------------|---------------------------------------------------|-----------------------------------------|
| `/addElement "Название"`            | Добавить корневую категорию                       | `/addElement "Электроника"`             |
| `/addElement "Родитель" "Дочерняя"` | Добавить дочернюю категорию                       | `/addElement "Электроника" "Смартфоны"` |
| `/removeElement "Название"`         | Удалить категорию                                 | `/removeElement "Смартфоны"`            |
| `/viewTree`                         | Показать дерево категорий                         | `/viewTree`                             |
| `/download`                         | Скачать Excel с категориями                       | `/download`                             |
| `/upload`                           | Загрузить из Excel (отправить файл после команды) | `/upload` + файл                        |
| `/help`                             | Показать справку                                  | `/help`                                 |

## Требования

- Java 17+
- PostgreSQL 12+
- Maven 3.6+

## Установка

1. Клонировать репозиторий:

```bash
git clone https://github.com/Rzavsky-ev/CategoryTreeBot
cd category-bot
```

2. Настроить БД в src/main/resources/application.properties:

```
spring.datasource.url=jdbc:postgresql://localhost:5432/categoryTree
spring.datasource.username=Eduard_Rz
spring.datasource.password=EdTelegramBot
```

3. Указать токен бота:

```
telegram.bot.token=8121785052:AAGoi_nmrkGSdyK69duapvY5IX7vZQNY5Co
```

4. Запустить приложение:

```
mvn spring-boot:run
```
Формат Excel-файла
```
Пример корректного файла:

id_Категории	Имя_Категории	id_Родителя
1	Электроника
2	Смартфоны	1
3	Ноутбуки	1
```

Пример работы
```
Пользователь: /addElement "Электроника"
Бот: ✅ Корневой элемент "Электроника" добавлен

Пользователь: /addElement "Электроника" "Смартфоны"
Бот: ✅ Дочерний элемент "Смартфоны" добавлен

Пользователь: /viewTree
Бот:
Дерево категорий:
- Электроника
    - Смартфоны
    - Ноутбуки
```