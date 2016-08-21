import React from 'react';


export default class StandardSummonerView extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        let championUrl = StandardSummonerView.urlBase + this.props.champion + ".png";
        return <div className="summoner-content">
            {(this.props.champion != null) ? <img src={championUrl}/> : null}
                <span>{"[" + this.props.server + "] "}</span>
                <span> {this.props.name} </span>
            </div>

    }
}

StandardSummonerView.defaultProps = {
    name: '',
    server: 'EUNE',
};

StandardSummonerView.propTypes = {
    name: React.PropTypes.string.isRequired,
    server: React.PropTypes.string.isRequired,
};

StandardSummonerView.urlBase = "http://ddragon.leagueoflegends.com/cdn/6.12.1/img/champion/";