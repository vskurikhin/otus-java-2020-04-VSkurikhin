#!/usr/bin/python3
# -*- coding: utf-8 -*-

import random
import pymysql.cursors
from itertools import combinations

WORK_PATH='/usr/share/dict/'

# Connect to the database
connection = pymysql.connect(host='localhost',
                             user='dbuser',
                             password='password',
                             db='db',
                             charset='utf8mb4',
                             cursorclass=pymysql.cursors.DictCursor)

try:
    with connection.cursor() as cursor:

        count = 0
        file1 = open(WORK_PATH + 'american-english, 'r')
        values = list()

        while True:
            count += 1

            # Get next line from file
            line = file1.readline()

            # if line is empty
            # end of file is reached
            if not line:
                break
            value = line.strip()
            values.append(value)

        file1.close()
        combinations = combinations(range(0, len(values)), 2)
        print(values)
        print(combinations)

        sql = "SELECT * FROM `user_profile` WHERE `id` > 10"
        cursor.execute(sql)
        result = cursor.fetchall()
        iterator = iter(combinations)

        for row in result:
            pair = next(iterator)
            first_name = values[pair[0]]
            sur_name = values[pair[1]]
            age = int(random.uniform(1, 99))
            # Create a new record
            sql = "INSERT INTO `user_info` (`id`, `first_name`, `sur_name`, `age`, `sex`, `city`) VALUES (%s, %s, %s, %s, %s, %s)"
            cursor.execute(sql, (row['id'], first_name, sur_name, age, 'M', 'Тестоград'))

    # connection is not autocommit by default. So you must commit to save
    # your changes.
    connection.commit()

finally:
    connection.close()
