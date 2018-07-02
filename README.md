# RouteMachine
Simple machine, that builds line based route between two real objects<br />
Тестовое задание для XPO Logistics<br />
Текст задания:<br />
Написать приложение для определения маршрута движения автомобиля между N ≤ 4 точек. Оконная форма должна содержать поля для внесения входной информации (наименований городов\населенных пунктов), вывод должен содержать:<br />
	координаты всех внесенных точек;<br />
	дистанцию на маршруте в км;<br />
	ориентировочное время в пути;<br />
Для получения геоданных можно пользоваться любыми бесплатными веб-сервисами, для получения данных о маршруте – open-source проектом project-osrm.org. Опционально: визуализация точек и линии движения на карте.<br />
<br />
Программа выполняет определение ключевых точек для построения пути между двумя реальными объектами (адресами/координатами)<br />
Для геокодирования используется открытое http api сервиса openstreetmaps<br />
Для построения точек пути используется открытое http api демо сервера проекта osrm.<br />
Дополнительные зависимости:<br />
java-json - для парсинга json файлов<br />
appache common - для округления координат<br />
