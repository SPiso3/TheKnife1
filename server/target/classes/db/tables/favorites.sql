CREATE TABLE IF NOT EXISTS Favorites (
    username VARCHAR(100) NOT NULL REFERENCES Users(username) ON UPDATE CASCADE ON DELETE CASCADE,
    restaurant_id SERIAL NOT NULL REFERENCES Restaurants(restaurant_id) ON UPDATE CASCADE ON DELETE CASCADE,
    PRIMARY KEY (username, restaurant_id)
);
