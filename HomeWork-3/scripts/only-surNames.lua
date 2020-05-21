math.randomseed(os.time())
math.random(); math.random(); math.random()

surNames = {'Adam', 'Barbara', 'Carl', 'Duke', 'Eben', 'Eve', 'Forest', 'Google', 'Zulu'}

request = function()
  path = "/public/search-users?surName=" .. surNames[math.random(9)]
  -- Return the request object with the current URL path
  return wrk.format('GET', path, {['Host'] = 'localhost'})
end
