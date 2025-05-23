CREATE TABLE IF NOT EXISTS Reviews (
    username VARCHAR(100) NOT NULL REFERENCES Users(username) ON UPDATE CASCADE ON DELETE NO ACTION,
    restaurant_id SERIAL NOT NULL REFERENCES Restaurants(restaurant_id) ON UPDATE CASCADE ON DELETE CASCADE,
    rating INT CHECK (rating BETWEEN 1 AND 5) NOT NULL,
    comment TEXT,
    reply TEXT,
    PRIMARY KEY (username, restaurant_id)
);
