math.randomseed(os.time())
math.random(); math.random(); math.random()

firstNames = {'Aachen', 'Aaliyah', 'Aaron', 'Abbas', 'Abbott', 'Abby', 'Abdul', 'Abe', 'Abidjan', 'Abigail', 'Abilene', 'Abner', 'Abraham', 'Abram', 'Absalom', 'Abuja', 'Abyssinia', 'Ac', 'Acevedo', 'Achaean', 'Achebe', 'Achernar', 'Acheson', 'Achilles', 'Aconcagua', 'Acosta', 'Acropolis', 'Acrux', 'Actaeon', 'Acton', 'Acts', 'Acuff', 'Ada', 'Addams', 'Adderley', 'Addie', 'Addison', 'Adela', 'Adele', 'Adeline', 'Aden', 'Adenauer', 'Adhara', 'Adidas', 'Adirondack', 'Adirondacks', 'Adkins', 'Adler', 'Adolf', 'Adolfo', 'Adolph', 'Adonis', 'Adonises', 'Adrian', 'Adriatic', 'Adrienne', 'Advent', 'Advil', 'Aegean', 'Aelfric', 'Aeneas', 'Aeneid', 'Aeolus', 'Aeroflot', 'Aeschylus', 'Aesculapius', 'Aesop', 'Afghan', 'Africa', 'Afrikaans', 'Afrikaner', 'Afrikaners', 'Afro', 'Afros', 'Ag'}
surNames = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'}

request = function()
  rangeFirstNames = table.getn(firstNames) - 1
  rangeSurNames = table.getn(surNames) - 1
  path = "/public/search-users?firstName=" .. firstNames[math.random(rangeFirstNames)] .. "&surName=" .. surNames[math.random(rangeSurNames)]
  -- Return the request object with the current URL path
  return wrk.format('GET', path, {['Host'] = 'localhost'})
end

logfile = io.open("/dev/shm/wrk-" .. os.date('%Y-%m-%d_%H_%M_%S') .. ".log", "a+");

response = function(status, header, body)
     logfile:write("status:" .. status .. "\n" .. body .. "\n-------------------------------------------------\n");
end
