# Database Structure
## theknife_db
- [addresses](./tables/addresses.sql) (<ins>address_id</ins>, country, city, street, house_number)
- [users](./tables/users.sql) (<ins>username</ins>, h_password, name, surname, birth_date, role, address_id<sup>[addresses](./tables/addresses.sql)</sup>)
- [restaurants](./tables/restaurants.sql) (<ins>restaurant_id</ins>, r_owner, r_name, avg_price, delivery, booking, r_type, latitude, longitude, address_id<sup>[addresses](./tables/addresses.sql)</sup>)
- [favorites](./tables/favorites.sql) (<ins>username<sup>[users](./tables/users.sql)</sup>, restaurant_id<sup>[restaurants](./tables/restaurants.sql)</sup></ins>)
- [reviews](./tables/reviews.sql) (<ins>username<sup>[users](./tables/users.sql)</sup>, restaurant_id<sup>[restaurants](./tables/restaurants.sql)</sup></ins>, rating, comment, reply)
