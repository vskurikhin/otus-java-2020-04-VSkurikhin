#!/usr/bin/python3
# -*- coding: utf-8 -*-

import pymysql.cursors

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
        for i in range(11, 26):
            print(i)
            file1 = open(WORK_PATH + 'american-english', 'r')

            while True:
                count += 1

                # Get next line from file
                line = file1.readline()

                # if line is empty
                # end of file is reached
                if not line:
                    break
                value = line.strip()

                # Create a new record
                sql = "INSERT INTO `user_profile` (`login`, `hash`) VALUES (%s, %s)"
                cursor.execute(sql, (value, value))

            file1.close()

    # connection is not autocommit by default. So you must commit to save
    # your changes.
    connection.commit()

finally:
    connection.close()
