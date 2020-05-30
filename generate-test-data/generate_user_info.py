#!/usr/bin/python3
# -*- coding: utf-8 -*-

import random
import pymysql.cursors
from itertools import combinations

WORK_PATH=''

# Connect to the database
connection = pymysql.connect(host='mysql.local',
                             user='dbuser',
                             password='password',
                             db='db',
                             charset='utf8mb4',
                             cursorclass=pymysql.cursors.DictCursor)

file1 = open(WORK_PATH + 'Names.shuf', 'r')
try:
    with connection.cursor() as cursor:

        count1 = 0
        values = list()

        while True:
            count1 += 1

            # Get next line from file
            line = file1.readline()

            # if line is empty
            # end of file is reached
            if not line:
                break
            value = line.strip()
            values.append(value)
            print(count1, sep=' ')

        combinations = combinations(range(0, len(values)), 2)
        print(values)
        print(combinations)

        sql = "SELECT * FROM `user_profile`"
        cursor.execute(sql)
        result = cursor.fetchall()
        iterator = iter(combinations)

        count2 = 0
        for row in result:
            count2 += 1

            pair = next(iterator)
            first_name = values[pair[0]]
            sur_name = values[pair[1]]
            age = int(random.uniform(1, 99))
            # Create a new record
            sql = "INSERT INTO `user_info` (`id`, `first_name`, `sur_name`, `age`, `sex`, `city`) VALUES (%s, %s, %s, %s, %s, %s)"
            print(sql)
            #cursor.execute(sql, (row['id'], first_name, sur_name, age, 'M', 'Тестоград'))
            print(count2, sep=' ')

    # connection is not autocommit by default. So you must commit to save
    # your changes.
    connection.commit()

finally:
    connection.close()
    file1.close()
