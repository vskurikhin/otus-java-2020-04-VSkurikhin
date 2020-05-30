math.randomseed(os.time())
math.random(); math.random(); math.random()

firstNames = {'Aconcagua', 'Afrocentrism', 'Agassiz', 'Al', 'Ana', 'Apia', 'Araucanian', 'Ari', 'Attica', 'Australopithecus', 'Baedeker',
 'Ball', 'Bar', 'Behring', 'Bologna', 'Bryce', 'Buckner', 'Bulfinch', 'Ca', 'Ch', 'Claiborne', 'Crimea', 'Cymbeline', 'Da', 'De', 'Di',
 'Elvi', 'Elvi', 'Elysiums', 'Er', 'Eugenia', 'Free', 'Frye', 'G', 'Ha', 'Hebrew', 'Hol', 'Hon', 'Imodium', 'Jacquard', 'Jenifer',
 'June', 'Karenina', 'Kerouac', 'Kowloon', 'Lippi', 'Lollard', 'Ma', 'Me', 'Minerva', 'Moroccan', 'Ne', 'Nickolas', 'No', 'Ono', 'Pa',
 'Protestantism', 'Re', 'Ro', 'Rumania', 'Sal', 'Sha', 'Sherri', 'Sivan', 'Speer', 'Squibb', 'Stuarts', 'Taurus', 'Toynbee', 'Tu',
 'Venusian', 'Wa', 'Wilkerson', 'Woods', 'Zapotec', 'Zionisms'}
surNames = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'}

request = function()
  rangeFirstNames = table.getn(firstNames)
  rangeSurNames = table.getn(surNames)
  path = "/public/search-users-with-interests?firstName=" .. firstNames[math.random(rangeFirstNames)] .. "&surName=" .. surNames[math.random(rangeSurNames)]
  -- Return the request object with the current URL path
  return wrk.format('GET', path, {['Host'] = 'hi-load-social-network.svn.su'})
end

logfile = io.open("wrk-" .. os.date('%Y-%m-%d_%H_%M_%S') .. ".log", "a+");

response = function(status, header, body)
     logfile:write("status:" .. status .. "\n" .. body .. "\n-------------------------------------------------\n");
end
