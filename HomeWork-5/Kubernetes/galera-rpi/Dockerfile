FROM arm32v7/debian:buster-slim

TAG vskurikhin/debian-buster-galera-armv7hf:latest
TAG vskurikhin/debian-buster-galera-armv7hf:v25.3.25-2-10.3.22-0
 
ENV LC_ALL C.UTF-8
ENV DEBIAN_FRONTEND noninteractive
ENV UDEV off

RUN mkdir -p /usr/share/man/man1 && echo 'APT::Get::Install-Recommends "false";\n\
APT::Get::Install-Suggests "false";' >> /etc/apt/apt.conf && echo 'APT::Get::Assume-Yes "true";\n\
APT::Install-Recommends "0";\n\
APT::Install-Suggests "0";\n\
quiet "true";' > /etc/apt/apt.conf.d/01_buildconfig

# add gosu for easy step-down from root
# https://github.com/tianon/gosu/releases
ENV GOSU_VERSION 1.12
COPY gosu-armhf /usr/local/bin/gosu

COPY entrypoint.sh /entrypoint.sh
COPY docker-entrypoint.sh /docker-entrypoint.sh
COPY report_status.sh /report_status.sh
COPY healthcheck.sh /healthcheck.sh
COPY xtrabackup-buster-bin.tgz /usr/local/xtrabackup-buster-bin.tgz

RUN apt-get update && apt-get install -y --no-install-recommends \
  sudo \
  ca-certificates \
  dnsutils \
  findutils \
  gnupg \
  dirmngr \
  jq \
  inetutils-ping \
  netbase \
  curl \
  udev \
  procps \
  pwgen \
  sysstat \
  $( \
      if apt-cache show 'iproute' 2>/dev/null | grep -q '^Version:'; then \
        echo 'iproute'; \
      else \
        echo 'iproute2'; \
      fi \
  ) \
  && apt install -y --no-install-recommends \
  galera-3 \
  gawk \
  libaio1 \
  libatomic1 \
  libconfig-inifiles-perl \
  libcurl3-gnutls \
  libdbi-perl \
  libev4 \
  libgdbm-compat4 \
  libgdbm6 \
  libmpfr6 \
  libperl5.28 \
  libpopt0 \
  libreadline5 \
  libsigsegv2 \
  libsnappy1v5 \
  libwrap0 \
  lsof \
  mariadb-client \
  mariadb-common \
  mariadb-server \
  mysql-common \
  perl \
  perl-modules-5.28 \
  psmisc \
  pwgen \
  rsync \
  socat \
  && rm -rf /var/lib/apt/lists/* \
  && c_rehash \
  && mkdir /docker-entrypoint-initdb.d \
  && rm -rf /var/cache/apt/archives/* /var/lib/apt/lists/* \
  && find /etc/mysql/ -type f | xargs sed -i 's/^\(bind-address\s*=\s*\).*$/\10.0.0.0/' \
  && ( cd /usr/local && tar xzf /usr/local/xtrabackup-buster-bin.tgz && rm -f /usr/local/xtrabackup-buster-bin.tgz )

# 01_nodoc
RUN echo 'path-exclude /usr/share/doc/*\n\
# we need to keep copyright files for legal reasons\n\
path-include /usr/share/doc/*/copyright\n\
path-exclude /usr/share/man/*\n\
path-exclude /usr/share/groff/*\n\
path-exclude /usr/share/info/*\n\
path-exclude /usr/share/lintian/*\n\
path-exclude /usr/share/linda/*\n\
path-exclude /usr/share/locale/*\n\
path-include /usr/share/locale/en*' > /etc/dpkg/dpkg.cfg.d/01_nodoc

VOLUME /var/lib/mysql

EXPOSE 3306 4444 4567 4568

ENTRYPOINT ["/entrypoint.sh"]
